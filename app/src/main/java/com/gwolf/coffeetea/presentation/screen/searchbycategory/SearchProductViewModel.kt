package com.gwolf.coffeetea.presentation.screen.searchbycategory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.domain.usecase.database.get.GetMinAndMaxProductsPriceByCategoryUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetProductsByCategoryWithFiltersUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class SearchProductScreenState(
    val categoryId: String = "",
    val productsList: List<Product> = listOf(),
    val categoryName: String = "",
    val textLow: String = "",
    val textHigh: String = "",
    val isDescending: Boolean = true,
    val minAndMaxPriceRange: ClosedFloatingPointRange<Float> = 0f..0f,
    val priceRangeState: ClosedFloatingPointRange<Float> = 0f..0f,
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
)

sealed class SearchProductIntent {
    data class EnterTextLow(val textLow: String) : SearchProductIntent()
    data class EnterTextHigh(val textHigh: String) : SearchProductIntent()
    data class ChangeSort(val isDescending: Boolean) : SearchProductIntent()
    data class ChangePriceRangeState(val priceRangeState: ClosedFloatingPointRange<Float>) : SearchProductIntent()
}

@HiltViewModel
class SearchProductViewModel @Inject constructor(
    private val getProductsByCategoryAndPriceUseCase: GetProductsByCategoryWithFiltersUseCase,
    private val getMinAndMaxProductPriceByCategoryUseCase: GetMinAndMaxProductsPriceByCategoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _state = MutableStateFlow(SearchProductScreenState())
    val state: StateFlow<SearchProductScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SearchProductScreenState()
    )

    fun onIntent(intent: SearchProductIntent) {
        when (intent) {
            is SearchProductIntent.EnterTextLow -> {
                _state.update { it.copy(textLow = intent.textLow) }
            }

            is SearchProductIntent.EnterTextHigh -> {
                _state.update { it.copy(textHigh = intent.textHigh) }
            }

            is SearchProductIntent.ChangeSort -> {
                _state.update { it.copy(isDescending = intent.isDescending) }
                viewModelScope.launch {
                    getProducts(_state.value.priceRangeState)
                }
            }

            is SearchProductIntent.ChangePriceRangeState -> {
                _state.update { it.copy(priceRangeState = intent.priceRangeState) }

                viewModelScope.launch {
                    getProducts(_state.value.priceRangeState)
                }
            }
        }
    }

    private suspend fun getMinAndMaxPriceRange() {
        getMinAndMaxProductPriceByCategoryUseCase.invoke(_state.value.categoryId)
            .collect { response ->
                when (response) {
                    is DataResult.Success -> {
                        _state.update {
                            it.copy(
                                minAndMaxPriceRange = response.data,
                                priceRangeState = response.data,
                                textLow = response.data.start.toString(),
                                textHigh = response.data.endInclusive.toString()
                            )
                        }
                        getProducts(response.data)
                    }

                    is DataResult.Error -> {
                        _state.update { it.copy(error = UiText.DynamicString(response.exception.message.orEmpty())) }
                    }
                }
            }
    }

    private suspend fun getProducts(priceRangeState: ClosedFloatingPointRange<Float>) {
        getProductsByCategoryAndPriceUseCase.invoke(
            categoryId = _state.value.categoryId,
            isDescending = _state.value.isDescending,
            priceRange = priceRangeState
        ).collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update { it.copy(productsList = response.data) }
                }

                is DataResult.Error -> {
                    _state.update { it.copy(error = UiText.DynamicString(response.exception.message.orEmpty())) }
                }
            }
        }
    }


    init {
        val category = savedStateHandle.toRoute<Screen.SearchByCategory>()

        _state.update {
            it.copy(
                isLoading = true,
                categoryId = category.categoryId,
                categoryName = category.categoryName
            )
        }
        viewModelScope.launch {
            val minAndMaxPriceRange = async { getMinAndMaxPriceRange() }

            try {
                awaitAll(minAndMaxPriceRange)
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Timber.d("Error loading search by category screen data: ${e.message}")
            }
        }
    }
}