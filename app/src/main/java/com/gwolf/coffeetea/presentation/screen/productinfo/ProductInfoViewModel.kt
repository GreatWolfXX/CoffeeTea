package com.gwolf.coffeetea.presentation.screen.productinfo

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.model.Product
import com.gwolf.coffeetea.domain.usecase.database.add.AddCartProductUseCase
import com.gwolf.coffeetea.domain.usecase.database.add.AddFavoriteUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetProductByIdUseCase
import com.gwolf.coffeetea.domain.usecase.database.remove.RemoveFavoriteUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.UiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductInfoUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false,
    val isInCart: Boolean = false,
    val error: String? = null,
)

sealed class ProductInfoEvent {
    data object AddToCart : ProductInfoEvent()
    data object AddFavorite : ProductInfoEvent()
    data object RemoveFavorite : ProductInfoEvent()
}

@HiltViewModel
class ProductInfoViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val addCartProductUseCase: AddCartProductUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _productInfoScreenState = mutableStateOf(ProductInfoUiState())
    val productInfoScreenState: State<ProductInfoUiState> = _productInfoScreenState

    fun onEvent(event: ProductInfoEvent) {
        when(event) {
            is ProductInfoEvent.AddToCart -> {
                addToCart()
            }
            is ProductInfoEvent.AddFavorite -> {
                addFavorite()
            }
            is ProductInfoEvent.RemoveFavorite -> {
                removeFavorite()
            }
        }
    }

    private fun addToCart() {
        viewModelScope.launch {
            // WARNING quantity hard code
            addCartProductUseCase.invoke(_productInfoScreenState.value.product?.id!!, 1)
                .collect { response ->
                    when (response) {
                        is UiResult.Success -> {
                            _productInfoScreenState.value =
                                _productInfoScreenState.value.copy(
                                    isInCart = true,
                                )
                        }

                        is UiResult.Error -> {
                            _productInfoScreenState.value =
                                _productInfoScreenState.value.copy(
                                    error = response.exception.message.toString(),
                                    isInCart = false
                                )
                        }
                    }
                }
        }
    }

    private fun removeFavorite() {
        viewModelScope.launch {
            removeFavoriteUseCase.invoke(_productInfoScreenState.value.product?.favoriteId!!)
                .collect { response ->
                    when (response) {
                        is UiResult.Success -> {
                            _productInfoScreenState.value =
                                _productInfoScreenState.value.copy(
                                    isFavorite = false,
                                )
                        }

                        is UiResult.Error -> {
                            _productInfoScreenState.value =
                                _productInfoScreenState.value.copy(
                                    error = response.exception.message.toString(),
                                    isFavorite = true
                                )
                        }
                    }
                }
        }
    }

    private fun addFavorite() {
        viewModelScope.launch {
            addFavoriteUseCase.invoke(_productInfoScreenState.value.product?.id!!)
                .collect { response ->
                    when (response) {
                        is UiResult.Success -> {
                            _productInfoScreenState.value =
                                _productInfoScreenState.value.copy(
                                    isFavorite = true,
                                )
                        }

                        is UiResult.Error -> {
                            _productInfoScreenState.value =
                                _productInfoScreenState.value.copy(
                                    error = response.exception.message.toString(),
                                    isFavorite = false
                                )
                        }
                    }
                }
        }
    }

    private suspend fun getProduct(productId: Int) {
        getProductByIdUseCase.invoke(productId).collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _productInfoScreenState.value =
                        _productInfoScreenState.value.copy(
                            product = response.data,
                            isFavorite = response.data?.favoriteId != -1,
                            isInCart = response.data?.cartId != -1
                        )
                }

                is UiResult.Error -> {
                    _productInfoScreenState.value =
                        _productInfoScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false,
                            isFavorite = false,
                            isInCart = false
                        )
                }
            }
        }
    }

    init {
        val productInfo = savedStateHandle.toRoute<Screen.ProductInfo>()

        _productInfoScreenState.value = _productInfoScreenState.value.copy(isLoading = true)
        viewModelScope.launch {
            val product = async { getProduct(productInfo.productId) }

            try {
                awaitAll(product)
                _productInfoScreenState.value = _productInfoScreenState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e("Coffee&TeaLogger", "Error loading product info screen data: ${e.message}")
            }
        }
    }

}