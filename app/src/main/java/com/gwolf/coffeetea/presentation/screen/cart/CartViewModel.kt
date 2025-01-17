package com.gwolf.coffeetea.presentation.screen.cart

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.model.Cart
import com.gwolf.coffeetea.domain.usecase.database.get.GetCartProductsListUseCase
import com.gwolf.coffeetea.domain.usecase.database.remove.RemoveCartProductUseCase
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UiResult
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
}


@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartProductsListUseCase: GetCartProductsListUseCase,
    private val removeCartProductUseCase: RemoveCartProductUseCase
) : ViewModel() {

    private val _cartScreenState = mutableStateOf(CartUiState())
    val cartScreenState: State<CartUiState> = _cartScreenState

    fun onEvent(event: CartEvent) {
        when (event) {
            is CartEvent.RemoveFromCart -> {
                removeFavorite(event.cartId)
            }
        }
    }

    private fun removeFavorite(cartId: String) {
        viewModelScope.launch {
            removeCartProductUseCase.invoke(cartId)
                .collect { response ->
                    when (response) {
                        is UiResult.Success -> {
                            val cartList = _cartScreenState.value.cartProductsList.filter {
                                it.cartId != cartId
                            }
                            _cartScreenState.value =
                                _cartScreenState.value.copy(
                                    cartProductsList = cartList
                                )
                        }

                        is UiResult.Error -> {
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
                is UiResult.Success -> {
                    _cartScreenState.value =
                        _cartScreenState.value.copy(
                            cartProductsList = response.data,
                        )
                }

                is UiResult.Error -> {
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