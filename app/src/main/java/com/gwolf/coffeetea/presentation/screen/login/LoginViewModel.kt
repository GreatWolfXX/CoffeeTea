package com.gwolf.coffeetea.presentation.screen.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.data.repository.local.PreferencesKey
import com.gwolf.coffeetea.domain.usecase.auth.SignInUseCase
import com.gwolf.coffeetea.domain.usecase.preference.SaveBooleanPreferenceUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateEmailUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidatePasswordUseCase
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val password: String = "",
    val passwordError: UiText? = null,
    val isRemember: Boolean = false,
    val passwordVisible: Boolean = false,
    val sigInSuccess: Boolean = false,
    val sigInError: UiText? = null,
    val isLoading: Boolean = false,
)

sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    data class IsRememberChanged(val isRemember: Boolean) : LoginEvent()
    data class PasswordVisibleChanged(val passwordVisible: Boolean) : LoginEvent()
    data object Submit : LoginEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val saveBooleanPreferenceUseCase: SaveBooleanPreferenceUseCase,
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _formState = mutableStateOf(LoginUiState())
    val formState: State<LoginUiState> = _formState

    fun onEvent(event: LoginEvent) {
        when(event) {
            is LoginEvent.EmailChanged -> {
                _formState.value = _formState.value.copy(email = event.email)
                validateEmail()
            }
            is LoginEvent.PasswordChanged -> {
                _formState.value = _formState.value.copy(password = event.password)
                validatePassword()
            }
            is LoginEvent.IsRememberChanged -> {
                _formState.value = _formState.value.copy(isRemember = event.isRemember)
            }
            is LoginEvent.PasswordVisibleChanged-> {
                _formState.value = _formState.value.copy(passwordVisible = event.passwordVisible)
            }
            is LoginEvent.Submit -> {
                if(validateEmail() && validatePassword()) {
                    signInUser()
                }
            }
        }
    }

    private fun signInUser() {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            signInUseCase.invoke(_formState.value.email, _formState.value.password).collect { result ->
                when(result) {
                    is UiResult.Success -> {
                        saveBooleanPreferenceUseCase.invoke(
                            key = PreferencesKey.rememberUserKey,
                            value = _formState.value.isRemember
                        )
                        _formState.value = _formState.value.copy(
                            sigInSuccess = true,
                            isLoading = false
                        )
                    }
                    is UiResult.Error -> {
                        _formState.value = _formState.value.copy(
                            sigInError = UiText.DynamicString(result.exception.message!!),
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

}