package com.gwolf.coffeetea.presentation.screen.forgotpassword

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.usecase.validate.ValidateEmailUseCase
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val forgotPasswordSuccess: Boolean = false,
    val forgotPasswordError: UiText? = null,
    val isLoading: Boolean = false
)

sealed class ForgotPasswordEvent {
    data class EmailChanged(val email: String) : ForgotPasswordEvent()
    data object Submit : ForgotPasswordEvent()
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
//    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    private val _formState = mutableStateOf(ForgotPasswordUiState())
    val formState: State<ForgotPasswordUiState> = _formState

    fun onEvent(event: ForgotPasswordEvent) {
        when(event) {
            is ForgotPasswordEvent.EmailChanged -> {
                _formState.value = _formState.value.copy(email = event.email)
                validateEmail()
            }
            is ForgotPasswordEvent.Submit -> {
                if(validateEmail()) {
                    forgotPassword()
                }
            }
        }
    }

    private fun forgotPassword() {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
//            forgotPasswordUseCase.invoke(_formState.value.email).collect { result ->
//                when(result) {
//                    is UiResult.Success -> {
//                        _formState.value = _formState.value.copy(
//                            forgotPasswordSuccess = true,
//                            isLoading = false
//                        )
//                    }
//
//                    is UiResult.Error -> {
//                        _formState.value = _formState.value.copy(
//                            forgotPasswordError = UiText.DynamicString(result.exception.message!!),
//                            isLoading = false
//                        )
//                    }
//                }
//            }
        }
    }

    private fun validateEmail(): Boolean {
        val emailResult = validateEmailUseCase.invoke(_formState.value.email)
        _formState.value = _formState.value.copy(emailError = emailResult.errorMessage)
        return emailResult.successful
    }

}