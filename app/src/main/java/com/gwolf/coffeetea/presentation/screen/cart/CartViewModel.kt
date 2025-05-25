package com.gwolf.coffeetea.presentation.screen.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.CartItem
import com.gwolf.coffeetea.domain.usecase.database.get.GetCartProductsUseCase
import com.gwolf.coffeetea.domain.usecase.database.remove.RemoveCartProductUseCase
import com.gwolf.coffeetea.domain.usecase.database.update.UpdateCartProductQuantityUseCase
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.LocalizedText
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

data class CartScreenState(
    val cartProductsList: List<CartItem> = listOf(),
    val isLoading: Boolean = false,
    val error: LocalizedText = LocalizedText.DynamicString(""),
)

sealed class CartIntent {
    data class RemoveFromCart(val cartItemId: String) : CartIntent()
    data class UpdateProductQuantity(val cartItemId: String, val quantity: Int) : CartIntent()
    data object Submit : CartIntent()
}

sealed class CartEvent {
    data object Idle : CartEvent()
    data object Navigate : CartEvent()
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartItemProductsListUseCase: GetCartProductsUseCase,
    private val removeCartItemProductUseCase: RemoveCartProductUseCase,
    private val updateCartItemProductQuantityUseCase: UpdateCartProductQuantityUseCase
) : ViewModel() {

    private var _state = MutableStateFlow(CartScreenState())
    val state: StateFlow<CartScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = CartScreenState()
    )

    private var _event: Channel<CartEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.RemoveFromCart -> {
                removeFromCart(intent.cartItemId)
            }

            is CartIntent.UpdateProductQuantity -> {
                updateCartItemProductQuantity(intent.cartItemId, intent.quantity)
            }

            is CartIntent.Submit -> {
                if (_state.value.cartProductsList.isNotEmpty()) {
                    viewModelScope.launch {
                        _event.send(CartEvent.Navigate)
                    }
                }
            }
        }
    }

    private fun removeFromCart(cartItemId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            removeCartItemProductUseCase.invoke(cartItemId)
                .collect { response ->
                    when (response) {
                        is DataResult.Success -> {
                            val cartList =
                                _state.value.cartProductsList.filter { it.id != cartItemId }
                            _state.update { it.copy(cartProductsList = cartList) }
                        }

                        is DataResult.Error -> {
                            _state.update { it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty())) }
                        }
                    }
                }
            _state.update {
                it.copy(
                    isLoading = false,
                    error = LocalizedText.DynamicString("")
                )
            }
        }
    }

    private fun updateCartItemProductQuantity(cartId: String, quantity: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            updateCartItemProductQuantityUseCase.invoke(cartId, quantity)
                .collect { response ->
                    when (response) {
                        is DataResult.Success -> {

                        }

                        is DataResult.Error -> {
                            _state.update { it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty())) }
                        }
                    }
                }
            _state.update {
                it.copy(
                    isLoading = false,
                    error = LocalizedText.DynamicString("")
                )
            }
        }
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
                            error = LocalizedText.DynamicString(response.exception.message.orEmpty())
                        )
                    }
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val product = async { getProducts() }

            try {
                awaitAll(product)
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Timber.d("Error loading cart screen data: ${e.message}")
            }
        }
    }
}