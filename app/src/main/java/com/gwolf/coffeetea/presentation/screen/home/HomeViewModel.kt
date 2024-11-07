package com.gwolf.coffeetea.presentation.screen.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.model.Category
import com.gwolf.coffeetea.domain.model.Product
import com.gwolf.coffeetea.domain.model.Promotion
import com.gwolf.coffeetea.domain.usecase.database.GetCategoriesListUseCase
import com.gwolf.coffeetea.domain.usecase.database.GetProductsListUseCase
import com.gwolf.coffeetea.domain.usecase.database.GetPromotionsListUseCase
import com.gwolf.coffeetea.util.UiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val promotionsList: List<Promotion> = listOf<Promotion>(),
    val categoriesList: List<Category> = listOf<Category>(),
    val productsList: List<Product> = listOf<Product>(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPromotionsListUseCase: GetPromotionsListUseCase,
    private val getCategoriesListUseCase: GetCategoriesListUseCase,
    private val getProductsListUseCase: GetProductsListUseCase
) : ViewModel() {

    private val _homeScreenState = mutableStateOf(HomeUiState())
    val homeScreenState: State<HomeUiState> = _homeScreenState

    private suspend fun getPromotions() {
        getPromotionsListUseCase.invoke().collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _homeScreenState.value =
                        _homeScreenState.value.copy(
                            promotionsList = response.data ?: listOf<Promotion>(),
                        )
                }

                is UiResult.Error -> {
                    _homeScreenState.value =
                        _homeScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }

    private suspend fun getCategories() {
        getCategoriesListUseCase.invoke().collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _homeScreenState.value =
                        _homeScreenState.value.copy(
                            categoriesList = response.data ?: listOf<Category>(),
                        )
                }

                is UiResult.Error -> {
                    _homeScreenState.value =
                        _homeScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }

    private suspend fun getProducts() {
        getProductsListUseCase.invoke().collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _homeScreenState.value =
                        _homeScreenState.value.copy(
                            productsList = response.data ?: listOf<Product>(),
                        )
                }

                is UiResult.Error -> {
                    _homeScreenState.value =
                        _homeScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }

    init {
        _homeScreenState.value = _homeScreenState.value.copy(isLoading = true)
        viewModelScope.launch {
            val promotionsList = async { getPromotions() }
            val categoriesList = async { getCategories() }
            val productsList = async { getProducts() }

            try {
                awaitAll(promotionsList, categoriesList, productsList)
                _homeScreenState.value = _homeScreenState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e("Coffee&TeaLogger", "Error loading home screen data: ${e.message}")
            }
        }
    }

}