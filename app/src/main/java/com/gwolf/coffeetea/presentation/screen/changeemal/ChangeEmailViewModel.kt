package com.gwolf.coffeetea.presentation.screen.changeemal

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.usecase.auth.VerifyOtpEmailUseCase
import com.gwolf.coffeetea.domain.usecase.database.update.ChangeEmailUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateEmailUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.LOGGER_TAG
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

data class ChangeEmailScreenState(
    val currentEmail: String = "",
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
    val otpError: UiText = UiText.DynamicString(""),
    val email: String = "",
    val emailError: UiText = UiText.DynamicString(""),
)

sealed class ChangeEmailIntent {
    sealed class Input {
        data class EnterEmail(val email: String) : ChangeEmailIntent()
    }

    sealed class ButtonClick {
        data object Submit : ChangeEmailIntent()
        data class CheckOtp(val otpToken: String) : ChangeEmailIntent()
        data object OnDismiss : ChangeEmailIntent()
    }
}

sealed class ChangeEmailEvent {
    data object Idle : ChangeEmailEvent()
    data object Navigate : ChangeEmailEvent()
    data object ShowOtp : ChangeEmailEvent()
    data object HideOtp : ChangeEmailEvent()
}

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val changeEmailUseCase: ChangeEmailUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val verifyOtpEmailUseCase: VerifyOtpEmailUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var _state = MutableStateFlow(ChangeEmailScreenState())
    val state: StateFlow<ChangeEmailScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = ChangeEmailScreenState()
    )

    private var _event: Channel<ChangeEmailEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: ChangeEmailIntent) {
        when (intent) {
            is ChangeEmailIntent.Input.EnterEmail -> {
                _state.update { it.copy(email = intent.email) }
                validateEmail()
            }

            is ChangeEmailIntent.ButtonClick.Submit -> {
                if (validateEmail()) {
                    sendOtpEmailChange()
                }
            }

            is ChangeEmailIntent.ButtonClick.CheckOtp -> {
                verifyOtpEmail(intent.otpToken)
            }

            is ChangeEmailIntent.ButtonClick.OnDismiss -> {
                viewModelScope.launch {
                    _event.send(ChangeEmailEvent.HideOtp)
                }
            }
        }
    }

    private fun sendOtpEmailChange() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            changeEmailUseCase.invoke(_state.value.email).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        _event.send(ChangeEmailEvent.ShowOtp)
                    }

                    is DataResult.Error -> {
                        _state.update {
                            it.copy(
                                error = UiText.DynamicString(result.exception.message.orEmpty())
                            )
                        }
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

    private fun verifyOtpEmail(otpToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            verifyOtpEmailUseCase.invoke(_state.value.email, otpToken).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        _event.send(ChangeEmailEvent.HideOtp)
                        _event.send(ChangeEmailEvent.Navigate)
                    }

                    is DataResult.Error -> {
                        _state.update {
                            it.copy(
                                otpError = UiText.DynamicString(result.exception.message.orEmpty()),
                            )
                        }
                    }
                }
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    otpError = UiText.DynamicString("")
                )
            }
        }
    }

    init {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val currentEmail = savedStateHandle.toRoute<Screen.ChangeEmail>().email

            try {
                _state.update {
                    it.copy(
                        isLoading = false,
                        currentEmail = currentEmail
                    )
                }
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading change email screen data: ${e.message}")
            }
        }
    }

    private fun validateEmail(): Boolean {
        val emailResult = validateEmailUseCase.invoke(_state.value.email)
        _state.update { it.copy(emailError = emailResult.errorMessage) }
        return emailResult.successful
    }
}