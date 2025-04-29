package com.gwolf.coffeetea.presentation.screen.checkout.pages

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.gwolf.coffeetea.domain.usecase.googlepay.IsReadyToGPayUseCase
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.getPaymentDataRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentUiState(
    val isGooglePayAvailable: Boolean = false,
    val paymentDataTask: Task<PaymentData>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class PaymentEvent {
    data class RequestPayment(val price: String) : PaymentEvent()
}
@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val isReadyToGPayUseCase: IsReadyToGPayUseCase,
    private val paymentsClient: PaymentsClient
) : ViewModel() {

    private val _paymentScreenState = mutableStateOf(PaymentUiState())
    val paymentScreenState: State<PaymentUiState> = _paymentScreenState
    
    fun onEvent(event: PaymentEvent) {
        when (event) {
            is PaymentEvent.RequestPayment -> {
                getLoadPaymentDataTask(event.price)
            }
        }
    }

    private suspend fun isGooglePayAvailable() {
        _paymentScreenState.value =
            _paymentScreenState.value.copy(
                isLoading = true
            )
        isReadyToGPayUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _paymentScreenState.value =
                        _paymentScreenState.value.copy(
                            isGooglePayAvailable = response.data,
                            isLoading = false
                        )
                }

                is DataResult.Error -> {
                    _paymentScreenState.value =
                        _paymentScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }

    private fun getLoadPaymentDataTask(price: String) {
        val paymentDataRequestJson = getPaymentDataRequest(price)
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        val task = paymentsClient.loadPaymentData(request)
        _paymentScreenState.value = _paymentScreenState.value.copy(
            paymentDataTask = task
        )
    }

    init {
        _paymentScreenState.value = _paymentScreenState.value.copy(isLoading = true)
        viewModelScope.launch {

            try {
                _paymentScreenState.value = _paymentScreenState.value.copy(isLoading = false)
                isGooglePayAvailable()
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading payment page data: ${e.message}")
            }
        }
    }
    
}