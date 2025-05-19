package com.gwolf.coffeetea.presentation.screen.changephone

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.usecase.database.update.ChangePhoneUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidatePhoneUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.UKRAINE_PHONE_CODE
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
import timber.log.Timber
import javax.inject.Inject

data class ChangePhoneScreenState(
    val currentPhone: String = "",
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
    val phone: String = "",
    val phoneError: UiText = UiText.DynamicString("")
)

sealed class ChangePhoneIntent {
    sealed class Input {
        data class EnterPhone(val phone: String) : ChangePhoneIntent()
    }

    sealed class ButtonClick {
        data object Submit : ChangePhoneIntent()
    }
}

sealed class ChangePhoneEvent {
    data object Idle : ChangePhoneEvent()
    data object Navigate : ChangePhoneEvent()
}

@HiltViewModel
class ChangePhoneViewModel @Inject constructor(
    private val changePhoneUseCase: ChangePhoneUseCase,
    private val validatePhoneUseCase: ValidatePhoneUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var _state = MutableStateFlow(ChangePhoneScreenState())
    val state: StateFlow<ChangePhoneScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = ChangePhoneScreenState()
    )

    private var _event: Channel<ChangePhoneEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: ChangePhoneIntent) {
        when (intent) {
            is ChangePhoneIntent.Input.EnterPhone -> {
                _state.update { it.copy(phone = intent.phone) }
                validatePhone()
            }

            is ChangePhoneIntent.ButtonClick.Submit -> {
                if (validatePhone()) {
                    sendOtpPhoneChange()
                }
            }
        }
    }

    private fun sendOtpPhoneChange() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val newPhone = "$UKRAINE_PHONE_CODE${_state.value.phone}"
            changePhoneUseCase.invoke(newPhone).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        _event.send(ChangePhoneEvent.Navigate)
                    }

                    is DataResult.Error -> {
                        _state.update { it.copy(error = UiText.DynamicString(result.exception.message.orEmpty())) }
                    }
                }
            }
        }
        _state.update {
            it.copy(
                isLoading = false,
                error = UiText.DynamicString(""),
            )
        }
    }

    init {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        isLoading = false,
                        currentPhone = savedStateHandle.toRoute<Screen.ChangePhone>().phone
                    )
                }
            } catch (e: Exception) {
                Timber.d("Error loading change phone screen data: ${e.message}")
            }
        }
    }

    private fun validatePhone(): Boolean {
        val newPhone = "$UKRAINE_PHONE_CODE${_state.value.phone}"
        val phoneResult = validatePhoneUseCase.invoke(newPhone, _state.value.currentPhone)
        _state.update { it.copy(phoneError = phoneResult.errorMessage) }
        return phoneResult.successful
    }
}