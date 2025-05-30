package com.gwolf.coffeetea.presentation.screen.productinfo

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
import com.gwolf.coffeetea.util.LocalizedText
import com.gwolf.coffeetea.util.PRODUCT_ADD_CART_QUANTITY
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

data class ProductInfoScreenState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false,
    val isInCart: Boolean = false,
    val error: LocalizedText = LocalizedText.DynamicString(""),
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
    private val addCartItemProductUseCase: AddCartProductUseCase,
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
            addCartItemProductUseCase.invoke(
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
                                    error = LocalizedText.DynamicString(response.exception.message.orEmpty()),
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
                            Timber.d("REMOVE FAVORITE")
                            _state.update {
                                it.copy(
                                    isFavorite = false,
                                    product = it.product?.copy(
                                        favoriteId = ""
                                    )
                                )
                            }
                        }

                        is DataResult.Error -> {
                            Timber.d("ERR REMOVE FAVORITE")
                            _state.update {
                                it.copy(
                                    error = LocalizedText.DynamicString(response.exception.message.orEmpty()),
                                    isFavorite = true
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun addFavorite() {
        Timber.d("ADD FAVORITE PRODUCT ${_state.value.product}")
        viewModelScope.launch {
            addFavoriteUseCase.invoke(_state.value.product?.id!!)
                .collect { response ->
                    when (response) {
                        is DataResult.Success -> {
                            Timber.d("ADD FAVORITE")
                            _state.update {
                                it.copy(
                                    isFavorite = true,
                                    product = it.product?.copy(
                                        favoriteId = response.data.id
                                    )
                                )
                            }
                        }

                        is DataResult.Error -> {
                            Timber.d("ERR ADD FAVORITE ${response.exception}")
                            _state.update {
                                it.copy(
                                    error = LocalizedText.DynamicString(response.exception.message.orEmpty()),
                                    isFavorite = false
                                )
                            }
                        }
                    }
                }
        }
    }

    private suspend fun getProduct(productId: String) {
        getProductByIdUseCase.invoke(productId).collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update {
                        it.copy(
                            product = response.data,
                            isFavorite = response.data.favoriteId.isNotBlank(),
                            isInCart = response.data.cartItemId.isNotBlank()
                        )
                    }
                }

                is DataResult.Error -> {
                    _state.update {
                        it.copy(
                            error = LocalizedText.DynamicString(response.exception.message.orEmpty()),
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
                Timber.d("Error loading product info screen data: ${e.message}")
            }
        }
    }

}