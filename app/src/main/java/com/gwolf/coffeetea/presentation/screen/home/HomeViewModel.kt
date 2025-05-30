package com.gwolf.coffeetea.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Category
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.domain.entities.Promotion
import com.gwolf.coffeetea.domain.usecase.database.add.AddCartProductUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetCategoriesUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetProductsUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetPromotionsUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.SearchProductsUseCase
import com.gwolf.coffeetea.util.ADD_TO_CART_COUNT
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.LocalizedText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class HomeScreenState(
    val promotionsList: List<Promotion> = listOf(),
    val categoriesList: List<Category> = listOf(),
    val productsList: List<Product> = listOf(),
    val searchProductsList: List<Product> = listOf(),
    val searchText: String = "",
    val isLoading: Boolean = false,
    val error: LocalizedText = LocalizedText.DynamicString(""),
)

sealed class HomeIntent {
    sealed class Input {
        data class Search(val query: String) : HomeIntent()
    }

    sealed class ButtonClick {
        data class AddToCart(val product: Product) : HomeIntent()
    }

    data object UpdateProducts : HomeIntent()
    data object ClearSearch : HomeIntent()
}

sealed class HomeEvent {
    data object Idle : HomeEvent()
    data object NavigateToCart : HomeEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPromotionsListUseCase: GetPromotionsUseCase,
    private val getCategoriesListUseCase: GetCategoriesUseCase,
    private val getProductsListUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val addCartItemProductUseCase: AddCartProductUseCase,
) : ViewModel() {

    private var _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = HomeScreenState()
    )

    private var _event: Channel<HomeEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.Input.Search -> {
                _state.update { it.copy(searchText = intent.query) }
            }

            is HomeIntent.ButtonClick.AddToCart -> {
                addToCart(intent.product)
            }

            is HomeIntent.UpdateProducts -> {
                viewModelScope.launch {
                    getProducts()
                }
            }

            is HomeIntent.ClearSearch -> {
                _state.update { it.copy(searchText = "") }
            }
        }
    }

    private fun addToCart(product: Product) {
        viewModelScope.launch {
            addCartItemProductUseCase.invoke(product.id, ADD_TO_CART_COUNT)
                .collect { response ->
                    when (response) {
                        is DataResult.Success -> {
                            val updatedProductsList =
                                _state.value.productsList.toMutableList()
                                    .apply {
                                        indexOfFirst { it.id == product.id }
                                            .takeIf { it != -1 }
                                            ?.let { index ->
                                                val cartProduct =
                                                    _state.value.productsList[index].copy(
                                                        id = response.data
                                                    )
                                                set(index, cartProduct)
                                            }
                                    }
                            _state.update { it.copy(productsList = updatedProductsList) }
                            _event.send(HomeEvent.NavigateToCart)
                        }

                        is DataResult.Error -> {
                            Timber.d("PRODUCT: ${response.exception}")
                            _state.update { it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty())) }
                        }
                    }
                }
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun setupSearchDebounce() {
         _state
             .map { it.searchText }
            .debounce(1000)
            .distinctUntilChanged()
            .onEach {
                _state.update { it.copy(searchProductsList = listOf()) }
            }
            .filter {
                it.isNotBlank()
            }
            .collect { query ->
                getSearchProducts(query)
            }
    }

    private suspend fun getPromotions() {
        getPromotionsListUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update { it.copy(promotionsList = response.data) }
                }

                is DataResult.Error -> {
                    _state.update { it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty())) }
                }
            }
        }
    }

    private suspend fun getCategories() {
        getCategoriesListUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update { it.copy(categoriesList = response.data) }
                }

                is DataResult.Error -> {
                    _state.update { it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty())) }
                }
            }
        }
    }

    private suspend fun getProducts() {
        getProductsListUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update { it.copy(productsList = response.data) }
                }

                is DataResult.Error -> {
                    _state.update { it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty())) }
                }
            }
        }
    }

    private fun getSearchProducts(search: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            searchProductsUseCase.invoke(search).collect { response ->
                when (response) {
                    is DataResult.Success -> {
                        _state.update { it.copy(searchProductsList = response.data) }
                    }

                    is DataResult.Error -> {
                        _state.update { it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty())) }
                    }
                }
            }
            _state.update {
                it.copy(
                    error = LocalizedText.DynamicString(""),
                    isLoading = false
                )
            }
        }
    }

    init {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val promotionsList = async { getPromotions() }
            val categoriesList = async { getCategories() }
            val productsList = async { getProducts() }

            try {
                awaitAll(promotionsList, categoriesList, productsList)
                _state.update { it.copy(isLoading = false) }

                setupSearchDebounce()
            } catch (e: Exception) {
                Timber.d("Error loading home screen data: ${e.message}")
            }
        }
    }
}