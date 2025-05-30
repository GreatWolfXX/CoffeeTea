package com.gwolf.coffeetea.presentation.screen.changeemal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.usecase.auth.VerifyOtpEmailUseCase
import com.gwolf.coffeetea.domain.usecase.database.update.ChangeEmailUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateEmailUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.LocalizedText
import dagger.hilt.android.lifecycle.HiltViewModel
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

data class ChangeEmailScreenState(
    val currentEmail: String = "",
    val isLoading: Boolean = false,
    val error: LocalizedText = LocalizedText.DynamicString(""),
    val showOtpModalSheet: Boolean = false,
    val otpError: LocalizedText = LocalizedText.DynamicString(""),
    val email: String = "",
    val emailError: LocalizedText = LocalizedText.DynamicString(""),
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
                    _state.update { it.copy(showOtpModalSheet = false) }
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
                        _state.update { it.copy(showOtpModalSheet = true) }
                    }

                    is DataResult.Error -> {
                        _state.update {
                            it.copy(
                                error = LocalizedText.DynamicString(result.exception.message.orEmpty())
                            )
                        }
                    }
                }
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    error = LocalizedText.DynamicString("")
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
                        _state.update { it.copy(showOtpModalSheet = false) }
                        _event.send(ChangeEmailEvent.Navigate)
                    }

                    is DataResult.Error -> {
                        _state.update {
                            it.copy(
                                otpError = LocalizedText.DynamicString(result.exception.message.orEmpty()),
                            )
                        }
                    }
                }
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    otpError = LocalizedText.DynamicString("")
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
                Timber.d("Error loading change email screen data: ${e.message}")
            }
        }
    }

    private fun validateEmail(): Boolean {
        val emailResult = validateEmailUseCase.invoke(_state.value.email)
        _state.update { it.copy(emailError = emailResult.errorMessage) }
        return emailResult.successful
    }
}