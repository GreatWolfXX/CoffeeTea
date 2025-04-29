package com.gwolf.coffeetea.presentation.screen.registration

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.usecase.auth.SignUpUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateEmailUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidatePasswordUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateRepeatPasswordUseCase
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegistrationUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val password: String = "",
    val passwordError: UiText? = null,
    val repeatPassword: String = "",
    val repeatPasswordError: UiText? = null,
    val passwordVisible: Boolean = false,
    val sigUpSuccess: Boolean = false,
    val sigUpError: UiText? = null,
    val isLoading: Boolean = false,
)

sealed class RegistrationEvent {
    data class EmailChanged(val email: String) : RegistrationEvent()
    data class PasswordChanged(val password: String) : RegistrationEvent()
    data class RepeatPasswordChanged(val repeatPassword: String) : RegistrationEvent()
    data class PasswordVisibleChanged(val passwordVisible: Boolean) : RegistrationEvent()
    data object Submit : RegistrationEvent()
}

sealed class ValidationEvent {
    data object Success: ValidationEvent()
}

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateRepeatPasswordUseCase: ValidateRepeatPasswordUseCase,
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _formState = mutableStateOf(RegistrationUiState())
    val formState: State<RegistrationUiState> = _formState

    fun onEvent(event: RegistrationEvent) {
        when(event) {
            is RegistrationEvent.EmailChanged -> {
                _formState.value = _formState.value.copy(email = event.email)
                validateEmail()
            }
            is RegistrationEvent.PasswordChanged -> {
                _formState.value = _formState.value.copy(password = event.password)
                validatePassword()
            }
            is RegistrationEvent.RepeatPasswordChanged-> {
                _formState.value = _formState.value.copy(repeatPassword = event.repeatPassword)
                validateRepeatPassword()
            }
            is RegistrationEvent.PasswordVisibleChanged-> {
                _formState.value = _formState.value.copy(passwordVisible = event.passwordVisible)
            }
            is RegistrationEvent.Submit -> {
                if(validateEmail() && validateRepeatPassword() && validatePassword()) {
                    signUpUser()
                }
            }
        }
    }

    private fun signUpUser() {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            signUpUseCase.invoke(_formState.value.email, _formState.value.password).collect { result ->
                when(result) {
                    is DataResult.Success -> {
                        _formState.value = _formState.value.copy(
                            sigUpSuccess = true,
                            isLoading = false
                        )
                    }
                    is DataResult.Error -> {
                        _formState.value = _formState.value.copy(
                            sigUpError = UiText.DynamicString(result.exception.message.orEmpty()),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun validateEmail(): Boolean {
        val emailResult = validateEmailUseCase.invoke(_formState.value.email)
        _formState.value = _formState.value.copy(emailError = emailResult.errorMessage)
        return emailResult.successful
    }

    private fun validatePassword(): Boolean {
        val passwordResult = validatePasswordUseCase.invoke(_formState.value.password)
        _formState.value = formState.value.copy(passwordError = passwordResult.errorMessage)
        return passwordResult.successful
    }

    private fun validateRepeatPassword(): Boolean {
        val passwordResult = validateRepeatPasswordUseCase.invoke(
            password = _formState.value.password,
            passwordRepeat = _formState.value.repeatPassword
        )
        _formState.value = formState.value.copy(repeatPasswordError = passwordResult.errorMessage)
        return passwordResult.successful
    }

}