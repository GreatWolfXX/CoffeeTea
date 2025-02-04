package com.gwolf.coffeetea.presentation.screen.aboutme

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.model.Profile
import com.gwolf.coffeetea.domain.usecase.database.add.AddImageProfileUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetProfileUseCase
import com.gwolf.coffeetea.domain.usecase.database.update.UpdateProfileImageUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateTextUseCase
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AboutMeUiState(
    val profile: Profile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val firstName: String = "",
    val firstNameError: UiText? = null,
    val lastName: String = "",
    val lastNameError: UiText? = null,
    val patronymic: String = "",
    val patronymicError: UiText? = null,
)

sealed class AboutMeEvent {
    data class FirstNameChanged(val firstName: String) : AboutMeEvent()
    data class LastNameChanged(val lastName: String) : AboutMeEvent()
    data class PatronymicChanged(val patronymic: String) : AboutMeEvent()
}

@HiltViewModel
class AboutMeViewModel @Inject constructor(
    private val auth: Auth,
    private val getProfileUseCase: GetProfileUseCase,
    private val addImageProfileUseCase: AddImageProfileUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase,
    private val validateTextUseCase: ValidateTextUseCase,
) : ViewModel() {

    private val _aboutMeScreenState = mutableStateOf(AboutMeUiState())
    val aboutMeScreenState: State<AboutMeUiState> = _aboutMeScreenState

    fun onEvent(event: AboutMeEvent) {
        when (event) {
            is AboutMeEvent.FirstNameChanged -> {
                _aboutMeScreenState.value = _aboutMeScreenState.value.copy(firstName = event.firstName)
                validateFirstName()
            }
            is AboutMeEvent.LastNameChanged -> {
                _aboutMeScreenState.value = _aboutMeScreenState.value.copy(lastName = event.lastName)
                validateLastName()
            }
            is AboutMeEvent.PatronymicChanged -> {
                _aboutMeScreenState.value = _aboutMeScreenState.value.copy(patronymic = event.patronymic)
                validatePatronymic()
            }
        }
    }

    private suspend fun getProfile() {
        getProfileUseCase.invoke().collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _aboutMeScreenState.value =
                        _aboutMeScreenState.value.copy(
                            profile = response.data,
                            firstName = response.data.firstName,
                            lastName = response.data.lastName,
                            patronymic = response.data.patronymic,
                        )
                }

                is UiResult.Error -> {
                    _aboutMeScreenState.value =
                        _aboutMeScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }

    init {
        _aboutMeScreenState.value = _aboutMeScreenState.value.copy(isLoading = true)
        viewModelScope.launch {
            val profile = async { getProfile() }

            try {
                awaitAll(profile)
                _aboutMeScreenState.value = _aboutMeScreenState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading about me screen data: ${e.message}")
            }
        }
    }

    private fun validateFirstName(): Boolean {
        val textResult = validateTextUseCase.invoke(_aboutMeScreenState.value.firstName)
        _aboutMeScreenState.value = _aboutMeScreenState.value.copy(firstNameError = textResult.errorMessage)
        return textResult.successful
    }

    private fun validateLastName(): Boolean {
        val textResult = validateTextUseCase.invoke(_aboutMeScreenState.value.lastName)
        _aboutMeScreenState.value = _aboutMeScreenState.value.copy(lastNameError = textResult.errorMessage)
        return textResult.successful
    }

    private fun validatePatronymic(): Boolean {
        val textResult = validateTextUseCase.invoke(_aboutMeScreenState.value.patronymic)
        _aboutMeScreenState.value = _aboutMeScreenState.value.copy(patronymicError = textResult.errorMessage)
        return textResult.successful
    }

}