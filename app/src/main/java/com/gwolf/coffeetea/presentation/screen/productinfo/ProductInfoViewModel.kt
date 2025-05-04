package com.gwolf.coffeetea.presentation.screen.productinfo

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.domain.usecase.database.add.AddCartProductUseCase
import com.gwolf.coffeetea.domain.usecase.database.add.AddFavoriteUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetProductByIdUseCase
import com.gwolf.coffeetea.domain.usecase.database.remove.RemoveFavoriteUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.PRODUCT_ADD_CART_QUANTITY
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
import javax.inject.Inject

data class ProductInfoScreenState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false,
    val isInCart: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
)

sealed class ProductInfoIntent {
    data object AddToCart : ProductInfoIntent()
    data object AddFavorite : ProductInfoIntent()
    data object RemoveFavorite : ProductInfoIntent()
}

sealed class ProductInfoEvent {
    data object Idle : ProductInfoEvent()
}

@HiltViewModel
class ProductInfoViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val addCartProductUseCase: AddCartProductUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var _state = MutableStateFlow(ProductInfoScreenState())
    val state: StateFlow<ProductInfoScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = ProductInfoScreenState()
    )

    private var _event: Channel<ProductInfoEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: ProductInfoIntent) {
        when (intent) {
            is ProductInfoIntent.AddToCart -> {
                addToCart()
            }

            is ProductInfoIntent.AddFavorite -> {
                addFavorite()
            }

            is ProductInfoIntent.RemoveFavorite -> {
                removeFavorite()
            }
        }
    }

    private fun addToCart() {
        viewModelScope.launch {
            addCartProductUseCase.invoke(
                _state.value.product?.id!!,
                PRODUCT_ADD_CART_QUANTITY
            )
                .collect { response ->
                    when (response) {
                        is DataResult.Success -> {
                            _state.update { it.copy(isInCart = true) }
                        }

                        is DataResult.Error -> {
                            _state.update {
                                it.copy(
                                    error = UiText.DynamicString(response.exception.message.orEmpty()),
                                    isInCart = false
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun removeFavorite() {
        viewModelScope.launch {
            removeFavoriteUseCase.invoke(_state.value.product?.favoriteId!!)
                .collect { response ->
                    when (response) {
                        is DataResult.Success -> {
                            _state.update { it.copy(isFavorite = false) }
                        }

                        is DataResult.Error -> {
                            _state.update {
                                it.copy(
                                    error = UiText.DynamicString(response.exception.message.orEmpty()),
                                    isFavorite = true
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun addFavorite() {
        viewModelScope.launch {
            addFavoriteUseCase.invoke(_state.value.product?.id!!)
                .collect { response ->
                    when (response) {
                        is DataResult.Success -> {
                            _state.update { it.copy(isFavorite = true) }
                        }

                        is DataResult.Error -> {
                            _state.update {
                                it.copy(
                                    error = UiText.DynamicString(response.exception.message.orEmpty()),
                                    isFavorite = false
                                )
                            }
                        }
                    }
                }
        }
    }

    private suspend fun getProduct(productId: Int) {
        getProductByIdUseCase.invoke(productId).collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update {
                        it.copy(
                            product = response.data,
                            isFavorite = response.data.favoriteId.isNotBlank(),
                            isInCart = response.data.cartId.isNotBlank()
                        )
                    }
                }

                is DataResult.Error -> {
                    _state.update {
                        it.copy(
                            error = UiText.DynamicString(response.exception.message.orEmpty()),
                            isFavorite = false,
                            isInCart = false
                        )
                    }
                }
            }
        }
    }

    init {
        val productInfo = savedStateHandle.toRoute<Screen.ProductInfo>()

        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val product = async { getProduct(productInfo.productId) }

            try {
                awaitAll(product)
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading product info screen data: ${e.message}")
            }
        }
    }

}