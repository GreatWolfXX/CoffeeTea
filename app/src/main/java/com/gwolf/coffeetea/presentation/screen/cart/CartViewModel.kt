package com.gwolf.coffeetea.presentation.screen.cart

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Cart
import com.gwolf.coffeetea.domain.usecase.database.get.GetCartProductsListUseCase
import com.gwolf.coffeetea.domain.usecase.database.remove.RemoveCartProductUseCase
import com.gwolf.coffeetea.domain.usecase.database.update.UpdateCartProductQuantityUseCase
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val cartProductsList: List<Cart> = listOf<Cart>(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class CartEvent {
    data class RemoveFromCart(val cartId: String) : CartEvent()
    data class UpdateProductQuantity(val cartId: String, val quantity: Int) : CartEvent()
}


@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartProductsListUseCase: GetCartProductsListUseCase,
    private val removeCartProductUseCase: RemoveCartProductUseCase,
    private val updateCartProductQuantityUseCase: UpdateCartProductQuantityUseCase
) : ViewModel() {

    private val _cartScreenState = mutableStateOf(CartUiState())
    val cartScreenState: State<CartUiState> = _cartScreenState

    fun onEvent(event: CartEvent) {
        when (event) {
            is CartEvent.RemoveFromCart -> {
                removeFavorite(event.cartId)
            }

            is CartEvent.UpdateProductQuantity -> {
                updateCartProductQuantity(event.cartId, event.quantity)
            }
        }
    }

    private fun removeFavorite(cartId: String) {
        viewModelScope.launch {
            removeCartProductUseCase.invoke(cartId)
                .collect { response ->
                    when (response) {
                        is DataResult.Success -> {
                            val cartList = _cartScreenState.value.cartProductsList.filter {
                                it.cartId != cartId
                            }
                            _cartScreenState.value =
                                _cartScreenState.value.copy(
                                    cartProductsList = cartList
                                )
                        }

                        is DataResult.Error -> {
                            _cartScreenState.value =
                                _cartScreenState.value.copy(
                                    error = response.exception.message.toString()
                                )
                        }
                    }
                }
        }
    }

    private fun updateCartProductQuantity(cartId: String, quantity: Int) {
        viewModelScope.launch {
            updateCartProductQuantityUseCase.invoke(cartId, quantity)
                .collect { response ->
                    when (response) {
                        is DataResult.Success -> {

                        }

                        is DataResult.Error -> {
                            _cartScreenState.value =
                                _cartScreenState.value.copy(
                                    error = response.exception.message.toString()
                                )
                        }
                    }
                }
        }
    }

    private suspend fun getProducts() {
        getCartProductsListUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _cartScreenState.value =
                        _cartScreenState.value.copy(
                            cartProductsList = response.data,
                        )
                }

                is DataResult.Error -> {
                    _cartScreenState.value =
                        _cartScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }

    init {
        _cartScreenState.value = _cartScreenState.value.copy(isLoading = true)
        viewModelScope.launch {
            val product = async { getProducts() }

            try {
                awaitAll(product)
                _cartScreenState.value = _cartScreenState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading cart screen data: ${e.message}")
            }
        }
    }

}