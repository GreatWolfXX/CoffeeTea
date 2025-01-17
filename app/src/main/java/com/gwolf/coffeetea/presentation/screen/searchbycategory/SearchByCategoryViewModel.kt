package com.gwolf.coffeetea.presentation.screen.searchbycategory

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.model.Product
import com.gwolf.coffeetea.domain.usecase.database.get.GetProductsByCategoryUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchByCategoryUiState(
    val productsList: List<Product> = listOf<Product>(),
    val categoryName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class SearchByCategoryViewModel @Inject constructor(
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchByCategoryScreenState = mutableStateOf(SearchByCategoryUiState())
    val searchByCategoryScreenState: State<SearchByCategoryUiState> = _searchByCategoryScreenState

    private suspend fun getProducts(categoryId: Int) {
        getProductsByCategoryUseCase.invoke(categoryId).collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _searchByCategoryScreenState.value =
                        _searchByCategoryScreenState.value.copy(
                            productsList = response.data,
                        )
                }

                is UiResult.Error -> {
                    _searchByCategoryScreenState.value =
                        _searchByCategoryScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }

    init {
        val category = savedStateHandle.toRoute<Screen.SearchByCategory>()

        _searchByCategoryScreenState.value = _searchByCategoryScreenState.value.copy(
            isLoading = true,
            categoryName = category.categoryName
        )
        viewModelScope.launch {
            val productsList = async { getProducts(category.categoryId) }

            try {
                awaitAll(productsList)
                _searchByCategoryScreenState.value = _searchByCategoryScreenState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading search by category screen data: ${e.message}")
            }
        }
    }

}