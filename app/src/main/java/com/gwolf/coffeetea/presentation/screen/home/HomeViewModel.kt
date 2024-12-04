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
import com.gwolf.coffeetea.domain.usecase.database.get.GetCategoriesListUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetProductsListUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetPromotionsListUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.SearchProductsUseCase
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
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPromotionsListUseCase: GetPromotionsListUseCase,
    private val getCategoriesListUseCase: GetCategoriesListUseCase,
    private val getProductsListUseCase: GetProductsListUseCase,
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    private val _homeScreenState = mutableStateOf(HomeUiState())
    val homeScreenState: State<HomeUiState> = _homeScreenState

    fun onSearchTextChange(text: String) {
        _homeScreenState.value = _homeScreenState.value.copy(
            searchText = text
        )
    }

    fun onEvent(event: HomeEvent) {
        when(event) {
            is HomeEvent.Search -> {
                _homeScreenState.value = _homeScreenState.value.copy(
                    searchText = event.query
                )
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

    private fun getSearchProducts(search: String) {
        viewModelScope.launch {
            searchProductsUseCase.invoke(search).collect { response ->
                Log.d("Coffee&TeaLogger", "Quary: $search Response: $response")
                when (response) {
                    is UiResult.Success -> {
                        _homeScreenState.value =
                            _homeScreenState.value.copy(
                                searchProductsList = response.data ?: listOf<Product>(),
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
                Log.e("Coffee&TeaLogger", "Error loading home screen data: ${e.message}")
            }
        }
    }

}