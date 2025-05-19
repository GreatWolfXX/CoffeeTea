package com.gwolf.coffeetea.presentation.screen.checkout.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.gwolf.coffeetea.domain.usecase.googlepay.IsReadyToGPayUseCase
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.UiText
import com.gwolf.coffeetea.util.getPaymentDataRequest
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

data class PaymentScreenState(
    val isGooglePayAvailable: Boolean = false,
    val paymentDataTask: Task<PaymentData>? = null,
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString("")
)

sealed class PaymentIntent {
    data class RequestPayment(val price: String) : PaymentIntent()
}

sealed class PaymentEvent {
    data object Idle : PaymentEvent()
}

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val isReadyToGPayUseCase: IsReadyToGPayUseCase,
    private val paymentsClient: PaymentsClient
) : ViewModel() {

    private var _state = MutableStateFlow(PaymentScreenState())
    val state: StateFlow<PaymentScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = PaymentScreenState()
    )

    private var _event: Channel<PaymentEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: PaymentIntent) {
        when (intent) {
            is PaymentIntent.RequestPayment -> {
                getLoadPaymentDataTask(intent.price)
            }
        }
    }

    private suspend fun isGooglePayAvailable() {
        isReadyToGPayUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update { it.copy(isGooglePayAvailable = response.data) }
                }

                is DataResult.Error -> {
                    _state.update { it.copy(error = UiText.DynamicString(response.exception.message.orEmpty())) }
                }
            }
        }
    }

    private fun getLoadPaymentDataTask(price: String) {
        val paymentDataRequestJson = getPaymentDataRequest(price)
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        val task = paymentsClient.loadPaymentData(request)
        _state.update { it.copy(paymentDataTask = task) }
    }

    init {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {

            try {
                isGooglePayAvailable()
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Timber.d("Error loading payment page data: ${e.message}")
            }
        }
    }

}