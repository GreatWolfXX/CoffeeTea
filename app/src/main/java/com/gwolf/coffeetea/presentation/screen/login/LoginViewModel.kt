package com.gwolf.coffeetea.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.data.repository.local.PreferencesKey
import com.gwolf.coffeetea.domain.usecase.auth.SignInUseCase
import com.gwolf.coffeetea.domain.usecase.preference.SaveBooleanPreferenceUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateEmailUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidatePasswordUseCase
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

data class LoginScreenState(
    val email: String = "",
    val emailError: UiText = UiText.DynamicString(""),
    val password: String = "",
    val passwordError: UiText = UiText.DynamicString(""),
    val isRemember: Boolean = false,
    val passwordVisible: Boolean = false,
    val signInError: UiText = UiText.DynamicString(""),
    val isLoading: Boolean = false,
)

sealed class LoginIntent {
    sealed class Input {
        data class EnterEmail(val email: String) : LoginIntent()
        data class EnterPassword(val password: String) : LoginIntent()
        data class IsRememberChanged(val isRemember: Boolean) : LoginIntent()
        data class PasswordVisibleChanged(val passwordVisible: Boolean) : LoginIntent()
    }

    sealed class ButtonClick {
        data object Submit : LoginIntent()
    }
}

sealed class LoginEvent {
    data object Idle : LoginEvent()
    data object Navigate : LoginEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val saveBooleanPreferenceUseCase: SaveBooleanPreferenceUseCase,
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private var _state = MutableStateFlow(LoginScreenState())
    val state: StateFlow<LoginScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = LoginScreenState()
    )

    private var _event: Channel<LoginEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Input.EnterEmail -> {
                _state.update { it.copy(email = intent.email) }
                validateEmail()
            }

            is LoginIntent.Input.EnterPassword -> {
                _state.update { it.copy(password = intent.password) }
                validatePassword()
            }

            is LoginIntent.Input.IsRememberChanged -> {
                _state.update { it.copy(isRemember = intent.isRemember) }
            }

            is LoginIntent.Input.PasswordVisibleChanged -> {
                _state.update { it.copy(passwordVisible = intent.passwordVisible) }
            }

            is LoginIntent.ButtonClick.Submit -> {
                if (validateEmail() && validatePassword()) {
                    signInUser()
                }
            }
        }
    }

    private fun signInUser() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            signInUseCase.invoke(_state.value.email, _state.value.password).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        saveBooleanPreferenceUseCase.invoke(
                            key = PreferencesKey.rememberUserKey,
                            value = _state.value.isRemember
                        )
                        _event.send(LoginEvent.Navigate)
                    }

                    is DataResult.Error -> {
                        _state.update { it.copy(signInError = UiText.DynamicString(result.exception.message.orEmpty())) }
                    }
                }
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    signInError = UiText.DynamicString("")
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
}