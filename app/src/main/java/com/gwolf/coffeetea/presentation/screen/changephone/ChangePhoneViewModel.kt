package com.gwolf.coffeetea.presentation.screen.changephone

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.usecase.database.update.ChangePhoneUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidatePhoneUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UKRAINE_PHONE_CODE
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangePhoneUiState(
    val currentPhone: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
//    val showOtpModalSheet: Boolean = false,
//    val otpError: UiText? = null,
    val phoneChanged: Boolean = false,
    val phone: String = "",
    val phoneError: UiText? = null
)

sealed class ChangePhoneEvent {
    data class PhoneChanged(val phone: String) : ChangePhoneEvent()
    data object Save : ChangePhoneEvent()
//    data class CheckOtp(val otpToken: String) : ChangePhoneEvent()
//    data object OnDismiss : ChangePhoneEvent()
}

@HiltViewModel
class ChangePhoneViewModel @Inject constructor(
    private val changePhoneUseCase: ChangePhoneUseCase,
    private val validatePhoneUseCase: ValidatePhoneUseCase,
//    private val verifyOtpPhoneUseCase: VerifyOtpPhoneUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _changePhoneState = mutableStateOf(ChangePhoneUiState())
    val changePhoneState: State<ChangePhoneUiState> = _changePhoneState

    fun onEvent(event: ChangePhoneEvent) {
        when (event) {
            is ChangePhoneEvent.PhoneChanged -> {
                _changePhoneState.value = _changePhoneState.value.copy(phone = event.phone)
                validatePhone()
            }

            is ChangePhoneEvent.Save -> {
                if (validatePhone()) {
                    sendOtpPhoneChange()
                }
            }

//            is ChangePhoneEvent.CheckOtp -> {
//                verifyOtpPhone(event.otpToken)
//            }
//
//            is ChangePhoneEvent.OnDismiss -> {
//                _changePhoneState.value = _changePhoneState.value.copy(showOtpModalSheet = false)
//            }
        }
    }

    private fun sendOtpPhoneChange() {
        viewModelScope.launch {
            _changePhoneState.value = _changePhoneState.value.copy(isLoading = true)
            val newPhone = "$UKRAINE_PHONE_CODE${_changePhoneState.value.phone}"
            changePhoneUseCase.invoke(newPhone).collect { result ->
                when (result) {
                    is UiResult.Success -> {
                        _changePhoneState.value = _changePhoneState.value.copy(
//                            showOtpModalSheet = true,
                            phoneChanged = true,
                            isLoading = false
                        )
                    }

                    is UiResult.Error -> {
                        _changePhoneState.value = _changePhoneState.value.copy(
                            error = result.exception.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

//    private fun verifyOtpPhone(otpToken: String) {
//        viewModelScope.launch {
//            _changePhoneState.value = _changePhoneState.value.copy(isLoading = true)
//            verifyOtpPhoneUseCase.invoke(_changePhoneState.value.phone, otpToken).collect { result ->
//                when (result) {
//                    is UiResult.Success -> {
//                        _changePhoneState.value = _changePhoneState.value.copy(
//                            showOtpModalSheet = false,
//                            phoneChanged = true,
//                            otpError = null,
//                            isLoading = false
//                        )
//                    }
//
//                    is UiResult.Error -> {
//                        _changePhoneState.value = _changePhoneState.value.copy(
//                            otpError = UiText.DynamicString(result.exception.message.orEmpty()),
//                            isLoading = false
//                        )
//                    }
//                }
//            }
//        }
//    }

    init {
        _changePhoneState.value = _changePhoneState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                _changePhoneState.value = _changePhoneState.value.copy(
                    isLoading = false,
                    currentPhone = savedStateHandle.toRoute<Screen.ChangePhone>().phone
                )
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading change phone screen data: ${e.message}")
            }
        }
    }

    private fun validatePhone(): Boolean {
        val newPhone = "$UKRAINE_PHONE_CODE${_changePhoneState.value.phone}"
        val phoneResult = validatePhoneUseCase.invoke(newPhone, _changePhoneState.value.currentPhone)
        _changePhoneState.value =
            _changePhoneState.value.copy(phoneError = phoneResult.errorMessage)
        return phoneResult.successful
    }

}