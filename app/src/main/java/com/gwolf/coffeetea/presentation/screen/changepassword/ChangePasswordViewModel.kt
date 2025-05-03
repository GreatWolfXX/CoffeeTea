package com.gwolf.coffeetea.presentation.screen.changepassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.usecase.database.update.ChangePasswordUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidatePasswordUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateRepeatPasswordUseCase
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

data class ChangePasswordScreenState(
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
    val passwordVisible: Boolean = false,
    val newPassword: String = "",
    val newPasswordError: UiText = UiText.DynamicString(""),
    val repeatNewPassword: String = "",
    val repeatNewPasswordError: UiText = UiText.DynamicString(""),
)

sealed class ChangePasswordIntent {
    sealed class Input {
        data class EnterNewPassword(val newPassword: String) : ChangePasswordIntent()
        data class EnterRepeatNewPassword(val newPassword: String) : ChangePasswordIntent()
    }

    sealed class ButtonClick {
        data class PasswordVisibleChanged(val passwordVisible: Boolean) : ChangePasswordIntent()
        data object Submit : ChangePasswordIntent()
    }
}

sealed class ChangePasswordEvent {
    data object Idle : ChangePasswordEvent()
    data object Navigate : ChangePasswordEvent()
}

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateRepeatPasswordUseCase: ValidateRepeatPasswordUseCase
) : ViewModel() {

    private var _state = MutableStateFlow(ChangePasswordScreenState())
    val state: StateFlow<ChangePasswordScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = ChangePasswordScreenState()
    )

    private var _event: Channel<ChangePasswordEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: ChangePasswordIntent) {
        when (intent) {
            is ChangePasswordIntent.Input.EnterNewPassword -> {
                _state.update { it.copy(newPassword = intent.newPassword) }
                validateNewPassword()
            }

            is ChangePasswordIntent.Input.EnterRepeatNewPassword -> {
                _state.update { it.copy(repeatNewPassword = intent.newPassword) }
                validateRepeatNewPassword()
            }

            is ChangePasswordIntent.ButtonClick.PasswordVisibleChanged -> {
                _state.update { it.copy(passwordVisible = intent.passwordVisible) }
            }

            is ChangePasswordIntent.ButtonClick.Submit -> {
                if (validateNewPassword() && validateRepeatNewPassword()) {
                    changePassword()
                }
            }
        }
    }

    private fun changePassword() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            changePasswordUseCase.invoke(_state.value.newPassword).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        _event.send(ChangePasswordEvent.Navigate)
                    }

                    is DataResult.Error -> {
                        _state.update { it.copy(repeatNewPasswordError = UiText.StringResource(R.string.err_new_password)) }
                    }
                }
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    repeatNewPasswordError = UiText.DynamicString("")
                )
            }
        }
    }

    init {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading change password screen data: ${e.message}")
            }
        }
    }

    private fun validateNewPassword(): Boolean {
        val passwordResult = validatePasswordUseCase.invoke(_state.value.newPassword)
        _state.update { it.copy(newPasswordError = passwordResult.errorMessage) }
        return passwordResult.successful
    }

    private fun validateRepeatNewPassword(): Boolean {
        val passwordResult = validateRepeatPasswordUseCase.invoke(
            password = _state.value.newPassword,
            passwordRepeat = _state.value.repeatNewPassword
        )
        _state.update { it.copy(repeatNewPasswordError = passwordResult.errorMessage) }
        return passwordResult.successful
    }
}