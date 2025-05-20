package com.gwolf.coffeetea.presentation.screen.checkout.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.gwolf.coffeetea.domain.entities.CartItem
import com.gwolf.coffeetea.domain.usecase.database.get.GetCartProductsUseCase
import com.gwolf.coffeetea.domain.usecase.googlepay.IsReadyToGPayUseCase
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.UiText
import com.gwolf.coffeetea.util.getPaymentDataRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    val cartProductsList: List<CartItem> = listOf(),
    val isGooglePayAvailable: Boolean = false,
    val paymentDataTask: Task<PaymentData>? = null,
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString("")
)

sealed class PaymentIntent {
    data object RequestPayment : PaymentIntent()
}

sealed class PaymentEvent {
    data object Idle : PaymentEvent()
}

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getCartItemProductsListUseCase: GetCartProductsUseCase,
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
                getLoadPaymentDataTask()
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

    private fun getLoadPaymentDataTask() {
        val paymentDataRequestJson = getPaymentDataRequest(state.value.cartProductsList)
        Timber.d("Payment Data Request: $paymentDataRequestJson")
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        val task = paymentsClient.loadPaymentData(request)
        _state.update { it.copy(paymentDataTask = task) }
    }

    private suspend fun getProducts() {
        getCartItemProductsListUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update { it.copy(cartProductsList = response.data) }
                }

                is DataResult.Error -> {
                    _state.update {
                        it.copy(
                            error = UiText.DynamicString(response.exception.message.orEmpty())
                        )
                    }
                }
            }
        }
    }

    init {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val product = async { getProducts() }

            try {
                awaitAll(product)
                isGooglePayAvailable()
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Timber.d("Error loading payment page data: ${e.message}")
            }
        }
    }

}