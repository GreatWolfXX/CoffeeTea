package com.gwolf.coffeetea.presentation.screen.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.usecase.auth.SignUpUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateEmailUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidatePasswordUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateRepeatPasswordUseCase
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegistrationScreenState(
    val email: String = "",
    val emailError: UiText = UiText.DynamicString(""),
    val password: String = "",
    val passwordError: UiText = UiText.DynamicString(""),
    val repeatPassword: String = "",
    val repeatPasswordError: UiText = UiText.DynamicString(""),
    val passwordVisible: Boolean = false,
    val signUpError: UiText = UiText.DynamicString(""),
    val isLoading: Boolean = false,
)

sealed class RegistrationIntent {
    sealed class Input {
        data class EnterEmail(val email: String) : RegistrationIntent()
        data class EnterPassword(val password: String) : RegistrationIntent()
        data class EnterRepeatPassword(val repeatPassword: String) : RegistrationIntent()
        data class PasswordVisibleChanged(val passwordVisible: Boolean) : RegistrationIntent()
    }

    sealed class ButtonClick {
        data object Submit : RegistrationIntent()
    }
}

sealed class RegistrationEvent {
    data object Idle : RegistrationEvent()
    data object Navigate : RegistrationEvent()
}

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateRepeatPasswordUseCase: ValidateRepeatPasswordUseCase,
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {
    
    private var _state = MutableStateFlow(RegistrationScreenState())
    val state: StateFlow<RegistrationScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = RegistrationScreenState()
    )

    private var _event: Channel<RegistrationEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: RegistrationIntent) {
        when (intent) {
            is RegistrationIntent.Input.EnterEmail -> {
                 _state.update { it.copy(email = intent.email) }
                validateEmail()
            }

            is RegistrationIntent.Input.EnterPassword -> {
                 _state.update { it.copy(password = intent.password) }
                validatePassword()
            }

            is RegistrationIntent.Input.EnterRepeatPassword -> {
                 _state.update { it.copy(repeatPassword = intent.repeatPassword) }
                validateRepeatPassword()
            }

            is RegistrationIntent.Input.PasswordVisibleChanged -> {
                 _state.update { it.copy(passwordVisible = intent.passwordVisible) }
            }

            is RegistrationIntent.ButtonClick.Submit -> {
                if (validateEmail() && validateRepeatPassword() && validatePassword()) {
                    signUpUser()
                }
            }
        }
    }

    private fun signUpUser() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            signUpUseCase.invoke(_state.value.email, _state.value.password).collect { result ->
                    when (result) {
                        is DataResult.Success -> {
                            _event.send(RegistrationEvent.Navigate)
                        }

                        is DataResult.Error -> {
                             _state.update { it.copy(signUpError = UiText.DynamicString(result.exception.message.orEmpty())) }
                        }
                    }
                }
            _state.update {
                it.copy(
                    isLoading = true,
                    signUpError = UiText.DynamicString("")
                )
            }
        }
    }

    private fun validateEmail(): Boolean {
        val emailResult = validateEmailUseCase.invoke(_state.value.email)
        _state.update { it.copy(emailError = emailResult.errorMessage) }
        return emailResult.successful
    }

    private fun validatePassword(): Boolean {
        val passwordResult = validatePasswordUseCase.invoke(_state.value.password)
        _state.update { it.copy(passwordError = passwordResult.errorMessage) }
        return passwordResult.successful
    }

    private fun validateRepeatPassword(): Boolean {
        val repeatPasswordResult = validateRepeatPasswordUseCase.invoke(
            password = _state.value.password,
            passwordRepeat = _state.value.repeatPassword
        )
        _state.update { it.copy(repeatPasswordError = repeatPasswordResult.errorMessage) }
        return repeatPasswordResult.successful
    }
}