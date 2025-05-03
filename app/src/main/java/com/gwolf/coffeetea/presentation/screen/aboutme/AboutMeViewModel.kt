package com.gwolf.coffeetea.presentation.screen.aboutme

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Profile
import com.gwolf.coffeetea.domain.usecase.database.get.GetProfileUseCase
import com.gwolf.coffeetea.domain.usecase.database.update.UpdateNameInfoUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateTextUseCase
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AboutMeScreenState(
    val profile: Profile? = null,
    val isLoading: Boolean = false,
    val updatedNameInfo: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
    val firstName: String = "",
    val firstNameError: UiText = UiText.DynamicString(""),
    val lastName: String = "",
    val lastNameError: UiText = UiText.DynamicString(""),
    val patronymic: String = "",
    val patronymicError: UiText = UiText.DynamicString(""),
)

sealed class AboutMeIntent {
    sealed class Input {
        data class EnterFirstName(val firstName: String) : AboutMeIntent()
        data class EnterLastName(val lastName: String) : AboutMeIntent()
        data class EnterPatronymic(val patronymic: String) : AboutMeIntent()
    }

    sealed class ButtonClick {
        data object Save : AboutMeIntent()
    }
}

sealed class AboutMeEvent {
    data object Idle : AboutMeEvent()
}

@HiltViewModel
class AboutMeViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateNameInfoUseCase: UpdateNameInfoUseCase,
    private val validateTextUseCase: ValidateTextUseCase,
) : ViewModel() {

    private var _state = MutableStateFlow(AboutMeScreenState())
    val state: StateFlow<AboutMeScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = AboutMeScreenState()
    )

    private var _event: Channel<AboutMeEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: AboutMeIntent) {
        when (intent) {
            is AboutMeIntent.Input.EnterFirstName -> {
                _state.update { it.copy(firstName = intent.firstName) }
                validateFirstName()
            }

            is AboutMeIntent.Input.EnterLastName -> {
                _state.update { it.copy(lastName = intent.lastName) }
                validateLastName()
            }

            is AboutMeIntent.Input.EnterPatronymic -> {
                _state.update { it.copy(patronymic = intent.patronymic) }
                validatePatronymic()
            }

            is AboutMeIntent.ButtonClick.Save -> {
                if (validateFirstName() && validateLastName() && validatePatronymic()) {
                    updateNameInfo()
                }
            }
        }
    }

    private suspend fun getProfile() {
        getProfileUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update {
                        it.copy(
                            profile = response.data,
                            firstName = response.data.firstName,
                            lastName = response.data.lastName,
                            patronymic = response.data.patronymic,
                        )
                    }
                }

                is DataResult.Error -> {
                    _state.update { it.copy(error = UiText.DynamicString(response.exception.message.orEmpty())) }
                }
            }
        }
    }

    private fun updateNameInfo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            updateNameInfoUseCase.invoke(
                firstName = _state.value.firstName,
                lastName = _state.value.lastName,
                patronymic = _state.value.patronymic
            ).collect { response ->
                when (response) {
                    is DataResult.Success -> {
                        _state.update { it.copy(updatedNameInfo = true) }
                    }

                    is DataResult.Error -> {
                        _state.update { it.copy(error = UiText.DynamicString(response.exception.message.orEmpty())) }
                    }
                }
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    error = UiText.DynamicString("")
                )
            }
        }
    }

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val profile = async { getProfile() }

            try {
                awaitAll(profile)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = UiText.DynamicString("")
                    )
                }
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading about me screen data: ${e.message}")
            }
        }
    }

    private fun validateFirstName(): Boolean {
        val textResult = validateTextUseCase.invoke(_state.value.firstName)
        _state.update { it.copy(firstNameError = textResult.errorMessage) }
        return textResult.successful
    }

    private fun validateLastName(): Boolean {
        val textResult = validateTextUseCase.invoke(_state.value.lastName)
        _state.update { it.copy(lastNameError = textResult.errorMessage) }
        return textResult.successful
    }

    private fun validatePatronymic(): Boolean {
        val textResult = validateTextUseCase.invoke(_state.value.patronymic)
        _state.update { it.copy(patronymicError = textResult.errorMessage) }
        return textResult.successful
    }
}