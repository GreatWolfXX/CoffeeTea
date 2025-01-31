package com.gwolf.coffeetea.presentation.screen.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.model.Category
import com.gwolf.coffeetea.domain.model.Product
import com.gwolf.coffeetea.domain.model.Promotion
import com.gwolf.coffeetea.domain.usecase.database.add.AddCartProductUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetCategoriesListUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetProductsListUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetPromotionsListUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.SearchProductsUseCase
import com.gwolf.coffeetea.util.ADD_TO_CART_COUNT
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val promotionsList: List<Promotion> = listOf<Promotion>(),
    val categoriesList: List<Category> = listOf<Category>(),
    val productsList: List<Product> = listOf<Product>(),
    val searchProductsList: List<Product> = listOf<Product>(),
    val searchText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class HomeEvent {
    data class Search(val query: String) : HomeEvent()
    data class AddToCart(val product: Product) : HomeEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPromotionsListUseCase: GetPromotionsListUseCase,
    private val getCategoriesListUseCase: GetCategoriesListUseCase,
    private val getProductsListUseCase: GetProductsListUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val addCartProductUseCase: AddCartProductUseCase,
) : ViewModel() {

    private val _homeScreenState = mutableStateOf(HomeUiState())
    val homeScreenState: State<HomeUiState> = _homeScreenState

    fun onSearchTextChange(text: String) {
        _homeScreenState.value = _homeScreenState.value.copy(
            searchText = text
        )
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Search -> {
                _homeScreenState.value = _homeScreenState.value.copy(
                    searchText = event.query
                )
            }

            is HomeEvent.AddToCart -> {
                addToCart(event.product)
            }
        }
    }

    private fun addToCart(product: Product) {
        viewModelScope.launch {
            addCartProductUseCase.invoke(product.id, ADD_TO_CART_COUNT)
                .collect { response ->
                    when (response) {
                        is UiResult.Success -> {
                            val indexProduct =
                                _homeScreenState.value.productsList.toMutableList().indexOfFirst {
                                    it.id == product.id
                                }
                            val updatedProductsList =
                                _homeScreenState.value.productsList.toMutableList().apply {
                                    this[indexProduct] =
                                        _homeScreenState.value.productsList[indexProduct].copy(
                                            cartId = response.data
                                        )
                                }
                            _homeScreenState.value = _homeScreenState.value.copy(
                                productsList = updatedProductsList
                            )
                        }

                        is UiResult.Error -> {
                            _homeScreenState.value =
                                _homeScreenState.value.copy(
                                    error = response.exception.message.toString()
                                )
                        }
                    }
                }
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun setupSearchDebounce() {
        snapshotFlow { _homeScreenState.value.searchText }
            .debounce(1000)
            .distinctUntilChanged()
            .onEach { query ->
                _homeScreenState.value =
                    _homeScreenState.value.copy(
                        searchProductsList = listOf<Product>()
                    )
            }
            .filter { it.isNotBlank() }
            .collect { query ->
                getSearchProducts(query)
            }
    }

    private suspend fun getPromotions() {
        getPromotionsListUseCase.invoke().collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _homeScreenState.value =
                        _homeScreenState.value.copy(
                            promotionsList = response.data,
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
                            categoriesList = response.data,
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
                            productsList = response.data,
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

    private fun getSearchProducts(search: String) {
        viewModelScope.launch {
            searchProductsUseCase.invoke(search).collect { response ->
                Log.d(LOGGER_TAG, "Query: $search Response: $response")
                when (response) {
                    is UiResult.Success -> {
                        _homeScreenState.value =
                            _homeScreenState.value.copy(
                                searchProductsList = response.data,
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

                setupSearchDebounce()
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading home screen data: ${e.message}")
            }
        }
    }

}