package com.gwolf.coffeetea.presentation.screen.changepassword

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.usecase.database.update.ChangePasswordUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidatePasswordUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateRepeatPasswordUseCase
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val passwordVisible: Boolean = false,
    val passwordChanged: Boolean = false,
    val newPassword: String = "",
    val newPasswordError: UiText? = null,
    val repeatNewPassword: String = "",
    val repeatNewPasswordError: UiText? = null
)

sealed class ChangePasswordEvent {
    data class NewPasswordChange(val newPassword: String) : ChangePasswordEvent()
    data class RepeatNewPasswordChange(val newPassword: String) : ChangePasswordEvent()
    data class PasswordVisibleChanged(val passwordVisible: Boolean) : ChangePasswordEvent()
    data object Save : ChangePasswordEvent()
}

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateRepeatPasswordUseCase: ValidateRepeatPasswordUseCase
) : ViewModel() {

    private val _changePasswordState = mutableStateOf(ChangePasswordUiState())
    val changePasswordState: State<ChangePasswordUiState> = _changePasswordState

    fun onEvent(event: ChangePasswordEvent) {
        when (event) {
            is ChangePasswordEvent.NewPasswordChange -> {
                _changePasswordState.value = _changePasswordState.value.copy(newPassword = event.newPassword)
                validateNewPassword()
            }

            is ChangePasswordEvent.RepeatNewPasswordChange -> {
                _changePasswordState.value = _changePasswordState.value.copy(repeatNewPassword = event.newPassword)
                validateRepeatNewPassword()
            }
            is ChangePasswordEvent.PasswordVisibleChanged-> {
                _changePasswordState.value = _changePasswordState.value.copy(passwordVisible = event.passwordVisible)
            }
            is ChangePasswordEvent.Save -> {
                if (validateNewPassword() && validateRepeatNewPassword()) {
                    changePassword()
                }
            }
        }
    }

    private fun changePassword() {
        viewModelScope.launch {
            _changePasswordState.value = _changePasswordState.value.copy(isLoading = true)
            changePasswordUseCase.invoke(_changePasswordState.value.newPassword).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        _changePasswordState.value = _changePasswordState.value.copy(
                            passwordChanged = true,
                            isLoading = false
                        )
                    }

                    is DataResult.Error -> {
                        _changePasswordState.value = _changePasswordState.value.copy(
                            repeatNewPasswordError = UiText.StringResource(R.string.err_new_password),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    init {
        _changePasswordState.value = _changePasswordState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                _changePasswordState.value = _changePasswordState.value.copy(
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading change password screen data: ${e.message}")
            }
        }
    }

    private fun validateNewPassword(): Boolean {
        val passwordResult = validatePasswordUseCase.invoke(_changePasswordState.value.newPassword)
        _changePasswordState.value = _changePasswordState.value.copy(newPasswordError = passwordResult.errorMessage)
        return passwordResult.successful
    }

    private fun validateRepeatNewPassword(): Boolean {
        val passwordResult = validateRepeatPasswordUseCase.invoke(
            password = _changePasswordState.value.newPassword,
            passwordRepeat = _changePasswordState.value.repeatNewPassword
        )
        _changePasswordState.value = _changePasswordState.value.copy(repeatNewPasswordError = passwordResult.errorMessage)
        return passwordResult.successful
    }

}