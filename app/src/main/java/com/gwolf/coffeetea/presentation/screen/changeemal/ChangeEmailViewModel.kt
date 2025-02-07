package com.gwolf.coffeetea.presentation.screen.changeemal

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.usecase.auth.VerifyOtpEmailUseCase
import com.gwolf.coffeetea.domain.usecase.update.ChangeEmailUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateEmailUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangeEmailUiState(
    val currentEmail: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val showOtpModalSheet: Boolean = false,
    val otpError: UiText? = null,
    val emailChanged: Boolean = false,
    val email: String = "",
    val emailError: UiText? = null
)

sealed class ChangeEmailEvent {
    data class EmailChanged(val email: String) : ChangeEmailEvent()
    data object Save : ChangeEmailEvent()
    data class CheckOtp(val otpToken: String) : ChangeEmailEvent()
    data object OnDismiss : ChangeEmailEvent()
}

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val changeEmailUseCase: ChangeEmailUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val verifyOtpEmailUseCase: VerifyOtpEmailUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _changeEmailState = mutableStateOf(ChangeEmailUiState())
    val changeEmailState: State<ChangeEmailUiState> = _changeEmailState

    fun onEvent(event: ChangeEmailEvent) {
        when (event) {
            is ChangeEmailEvent.EmailChanged -> {
                _changeEmailState.value = _changeEmailState.value.copy(email = event.email)
                validateEmail()
            }

            is ChangeEmailEvent.Save -> {
                if (validateEmail()) {
                    sendOtpEmailChange()
                }
            }

            is ChangeEmailEvent.CheckOtp -> {
                verifyOtpEmail(event.otpToken)
            }

            is ChangeEmailEvent.OnDismiss -> {
                _changeEmailState.value = _changeEmailState.value.copy(showOtpModalSheet = false)
            }
        }
    }

    private fun sendOtpEmailChange() {
        viewModelScope.launch {
            _changeEmailState.value = _changeEmailState.value.copy(isLoading = true)
            changeEmailUseCase.invoke(_changeEmailState.value.email).collect { result ->
                when (result) {
                    is UiResult.Success -> {
                        _changeEmailState.value = _changeEmailState.value.copy(
                            showOtpModalSheet = true,
                            isLoading = false
                        )
                    }

                    is UiResult.Error -> {
                        _changeEmailState.value = _changeEmailState.value.copy(
                            error = result.exception.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun verifyOtpEmail(otpToken: String) {
        viewModelScope.launch {
            _changeEmailState.value = _changeEmailState.value.copy(isLoading = true)
            verifyOtpEmailUseCase.invoke(_changeEmailState.value.email, otpToken).collect { result ->
                when (result) {
                    is UiResult.Success -> {
                        _changeEmailState.value = _changeEmailState.value.copy(
                            showOtpModalSheet = false,
                            emailChanged = true,
                            otpError = null,
                            isLoading = false
                        )
                    }

                    is UiResult.Error -> {
                        _changeEmailState.value = _changeEmailState.value.copy(
                            otpError = UiText.DynamicString(result.exception.message.orEmpty()),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    init {
        _changeEmailState.value = _changeEmailState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                _changeEmailState.value = _changeEmailState.value.copy(
                    isLoading = false,
                    currentEmail = savedStateHandle.toRoute<Screen.ChangeEmail>().email
                )
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading change email screen data: ${e.message}")
            }
        }
    }

    private fun validateEmail(): Boolean {
        val emailResult = validateEmailUseCase.invoke(_changeEmailState.value.email)
        _changeEmailState.value =
            _changeEmailState.value.copy(emailError = emailResult.errorMessage)
        return emailResult.successful
    }

}