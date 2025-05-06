package com.gwolf.coffeetea.presentation.screen.searchbycategory

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.domain.usecase.database.get.GetProductsByCategoryUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.LOGGER_TAG
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
import javax.inject.Inject

data class SearchProductScreenState(
    val productsList: List<Product> = listOf(),
    val categoryName: String = "",
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
)

@HiltViewModel
class SearchProductViewModel @Inject constructor(
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _state = MutableStateFlow(SearchProductScreenState())
    val state: StateFlow<SearchProductScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SearchProductScreenState()
    )

    private suspend fun getProducts(categoryId: Int) {
        getProductsByCategoryUseCase.invoke(categoryId).collect { response ->
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
                categoryName = category.categoryName
            )
        }
        viewModelScope.launch {
            val productsList = async { getProducts(category.categoryId) }

            try {
                awaitAll(productsList)
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading search by category screen data: ${e.message}")
            }
        }
    }
}