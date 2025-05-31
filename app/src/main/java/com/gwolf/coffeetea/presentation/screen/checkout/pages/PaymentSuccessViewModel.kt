package com.gwolf.coffeetea.presentation.screen.checkout.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.util.LocalizedText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentSuccessScreenState(
    val isLoading: Boolean = false,
    val error: LocalizedText = LocalizedText.DynamicString(""),
)

sealed class PaymentSuccessIntent {
    data object Submit : PaymentSuccessIntent()
}

sealed class PaymentSuccessEvent {
    data object Idle : PaymentSuccessEvent()
    data object Navigate : PaymentSuccessEvent()
}

@HiltViewModel
class PaymentSuccessViewModel @Inject constructor() : ViewModel() {

    private var _state = MutableStateFlow(PaymentSuccessScreenState())
    val state: StateFlow<PaymentSuccessScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = PaymentSuccessScreenState()
    )

    private var _event: Channel<PaymentSuccessEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: PaymentSuccessIntent) {
        when (intent) {
            is PaymentSuccessIntent.Submit -> {
                viewModelScope.launch {
                    _event.send(PaymentSuccessEvent.Navigate)
                }
            }
        }
    }
}