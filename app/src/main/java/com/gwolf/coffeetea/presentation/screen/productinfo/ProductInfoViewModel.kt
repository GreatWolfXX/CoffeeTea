package com.gwolf.coffeetea.presentation.screen.productinfo

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.model.Product
import com.gwolf.coffeetea.domain.usecase.database.GetProductByIdUseCase
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
    val error: String? = null,
)

@HiltViewModel
class ProductInfoViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _productInfoScreenState = mutableStateOf(ProductInfoUiState())
    val productInfoScreenState: State<ProductInfoUiState> = _productInfoScreenState

    private suspend fun getProduct(productId: Int) {
        getProductByIdUseCase.invoke(productId).collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _productInfoScreenState.value =
                        _productInfoScreenState.value.copy(
                            product = response.data,
                        )
                }

                is UiResult.Error -> {
                    _productInfoScreenState.value =
                        _productInfoScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
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