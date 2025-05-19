package com.gwolf.coffeetea.presentation.screen.checkout.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Profile
import com.gwolf.coffeetea.domain.usecase.database.get.GetProfileUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidatePhoneUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateTextUseCase
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.UKRAINE_PHONE_CODE
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
import timber.log.Timber
import javax.inject.Inject

data class PersonalInfoScreenState(
    val profile: Profile? = null,
    val phone: String = "",
    val phoneError: UiText = UiText.DynamicString(""),
    val firstName: String = "",
    val firstNameError: UiText = UiText.DynamicString(""),
    val lastName: String = "",
    val lastNameError: UiText = UiText.DynamicString(""),
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
)

sealed class PersonalInfoIntent {
    sealed class Input {
        data class EnterPhone(val phone: String) : PersonalInfoIntent()
        data class EnterFirstName(val firstName: String) : PersonalInfoIntent()
        data class EnterLastName(val lastName: String) : PersonalInfoIntent()
    }

    sealed class ButtonClick {
        data object Submit : PersonalInfoIntent()
    }
}

sealed class PersonalInfoEvent {
    data object Idle : PersonalInfoEvent()
    data object Navigate : PersonalInfoEvent()
}

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val validatePhoneUseCase: ValidatePhoneUseCase,
    private val validateTextUseCase: ValidateTextUseCase,
    private val getProfileUseCase: GetProfileUseCase,
) : ViewModel() {

    private var _state = MutableStateFlow(PersonalInfoScreenState())
    val state: StateFlow<PersonalInfoScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = PersonalInfoScreenState()
    )

    private var _event: Channel<PersonalInfoEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: PersonalInfoIntent) {
        when (intent) {
            is PersonalInfoIntent.Input.EnterPhone -> {
                _state.update { it.copy(phone = intent.phone) }
                validatePhone()
            }

            is PersonalInfoIntent.Input.EnterFirstName -> {
                _state.update { it.copy(firstName = intent.firstName) }
                validateFirstName()
            }

            is PersonalInfoIntent.Input.EnterLastName -> {
                _state.update { it.copy(lastName = intent.lastName) }
                validateLastName()
            }

            is PersonalInfoIntent.ButtonClick.Submit -> {
                viewModelScope.launch {
                    _event.send(PersonalInfoEvent.Navigate)
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
                            phone = response.data.phone.removePrefix("+38"),
                            firstName = response.data.firstName,
                            lastName = response.data.lastName
                        )
                    }
                }

                is DataResult.Error -> {
                    _state.update { it.copy(error = UiText.DynamicString(response.exception.message.orEmpty())) }
                }
            }
        }
    }

    init {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val profile = async { getProfile() }

            try {
                awaitAll(profile)

                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Timber.d("Error loading personal info page data: ${e.message}")
            }
        }
    }

    private fun validatePhone(): Boolean {
        val newPhone = "$UKRAINE_PHONE_CODE${_state.value.phone}"
        val phoneResult = validatePhoneUseCase.invoke(newPhone)
        _state.update { it.copy(phoneError = phoneResult.errorMessage) }
        return phoneResult.successful
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
}