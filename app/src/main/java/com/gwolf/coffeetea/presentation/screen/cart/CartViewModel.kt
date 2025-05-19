package com.gwolf.coffeetea.presentation.screen.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Cart
import com.gwolf.coffeetea.domain.usecase.database.get.GetCartProductsUseCase
import com.gwolf.coffeetea.domain.usecase.database.remove.RemoveCartProductUseCase
import com.gwolf.coffeetea.domain.usecase.database.update.UpdateCartProductQuantityUseCase
import com.gwolf.coffeetea.presentation.screen.login.LoginEvent
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.UiText
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
    val cartProductsList: List<Cart> = listOf(),
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
)

sealed class CartIntent {
    data class RemoveFromCart(val cartId: String) : CartIntent()
    data class UpdateProductQuantity(val cartId: String, val quantity: Int) : CartIntent()
    data object Submit : CartIntent()
}

sealed class CartEvent {
    data object Idle : CartEvent()
    data object Navigate : CartEvent()
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartProductsListUseCase: GetCartProductsUseCase,
    private val removeCartProductUseCase: RemoveCartProductUseCase,
    private val updateCartProductQuantityUseCase: UpdateCartProductQuantityUseCase
) : ViewModel() {

    private var _state = MutableStateFlow(CartScreenState())
    val state: StateFlow<CartScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = CartScreenState()
    )

    private var _event: Channel<LoginEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.RemoveFromCart -> {
                removeFavorite(intent.cartId)
            }

            is CartIntent.UpdateProductQuantity -> {
                updateCartProductQuantity(intent.cartId, intent.quantity)
            }

            is CartIntent.Submit -> {
                if (_state.value.cartProductsList.isNotEmpty()) {
                    viewModelScope.launch {
                        _event.send(LoginEvent.Navigate)
                    }
                }
            }
        }
    }

    private fun removeFavorite(cartId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            removeCartProductUseCase.invoke(cartId)
                .collect { response ->
                    when (response) {
                        is DataResult.Success -> {
                            val cartList =
                                _state.value.cartProductsList.filter { it.cartId != cartId }
                            _state.update { it.copy(cartProductsList = cartList) }
                        }

                        is DataResult.Error -> {
                            _state.update { it.copy(error = UiText.DynamicString(response.exception.message.orEmpty())) }
                        }
                    }
                }
            _state.update {
                it.copy(
                    isLoading = false,
                    error = UiText.DynamicString("")
                )
            }
        }
    }

    private fun updateCartProductQuantity(cartId: String, quantity: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            updateCartProductQuantityUseCase.invoke(cartId, quantity)
                .collect { response ->
                    when (response) {
                        is DataResult.Success -> {

                        }

                        is DataResult.Error -> {
                            _state.update { it.copy(error = UiText.DynamicString(response.exception.message.orEmpty())) }
                        }
                    }
                }
            _state.update {
                it.copy(
                    isLoading = false,
                    error = UiText.DynamicString("")
                )
            }
        }
    }

    private suspend fun getProducts() {
        getCartProductsListUseCase.invoke().collect { response ->
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