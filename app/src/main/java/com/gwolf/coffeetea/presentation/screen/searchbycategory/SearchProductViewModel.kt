package com.gwolf.coffeetea.presentation.screen.searchbycategory

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.domain.usecase.database.get.GetProductsByCategoryUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.LOGGER_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchProductUiState(
    val productsList: List<Product> = listOf<Product>(),
    val categoryName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class SearchProductViewModel @Inject constructor(
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchProductScreenState = mutableStateOf(SearchProductUiState())
    val searchProductScreenState: State<SearchProductUiState> = _searchProductScreenState

    private suspend fun getProducts(categoryId: Int) {
        getProductsByCategoryUseCase.invoke(categoryId).collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _searchProductScreenState.value =
                        _searchProductScreenState.value.copy(
                            productsList = response.data,
                        )
                }

                is DataResult.Error -> {
                    _searchProductScreenState.value =
                        _searchProductScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }

    init {
        val category = savedStateHandle.toRoute<Screen.SearchByCategory>()

        _searchProductScreenState.value = _searchProductScreenState.value.copy(
            isLoading = true,
            categoryName = category.categoryName
        )
        viewModelScope.launch {
            val productsList = async { getProducts(category.categoryId) }

            try {
                awaitAll(productsList)
                _searchProductScreenState.value = _searchProductScreenState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading search by category screen data: ${e.message}")
            }
        }
    }

}