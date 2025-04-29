package com.gwolf.coffeetea.presentation.screen.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.usecase.validate.ValidateEmailUseCase
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

data class ForgotPasswordScreenState(
    val email: String = "",
    val emailError: UiText = UiText.DynamicString(""),
    val forgotPasswordSuccess: Boolean = false,
    val forgotPasswordError: UiText = UiText.DynamicString(""),
    val isLoading: Boolean = false
)

sealed class ForgotPasswordIntent {
    sealed class Input {
        data class EnterEmail(val email: String) : ForgotPasswordIntent()
    }

    sealed class ButtonClick {
        data object Submit : ForgotPasswordIntent()
    }
}

sealed class ForgotPasswordEvent {
    data object Idle : ForgotPasswordEvent()
    data object Navigate : ForgotPasswordEvent()
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
//    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {
    
    private var _state = MutableStateFlow(ForgotPasswordScreenState())
    val state: StateFlow<ForgotPasswordScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = ForgotPasswordScreenState()
    )

    private var _event: Channel<ForgotPasswordEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: ForgotPasswordIntent) {
        when(intent) {
            is ForgotPasswordIntent.Input.EnterEmail -> {
               _state.update { it.copy(email = intent.email) }
                validateEmail()
            }
            is ForgotPasswordIntent.ButtonClick.Submit -> {
                if(validateEmail()) {
                    forgotPassword()
                }
            }
        }
    }

    private fun forgotPassword() {
        viewModelScope.launch {
           _state.update { it.copy(isLoading = true) }
//            forgotPasswordUseCase.invoke(_state.value.email).collect { result ->
//                when(result) {
//                    is DataResult.Success -> {
//                       _state.update { it.copy(
//                            forgotPasswordSuccess = true,
//                            isLoading = false
//                        )
//                    }
//
//                    is DataResult.Error -> {
//                       _state.update { it.copy(
//                            forgotPasswordError = UiText.DynamicString(result.exception.message!!),
//                            isLoading = false
//                        )
//                    }
//                }
//            }
        }
    }

    private fun validateEmail(): Boolean {
        val emailResult = validateEmailUseCase.invoke(_state.value.email)
        _state.update { it.copy(emailError = emailResult.errorMessage) }
        return emailResult.successful
    }
}