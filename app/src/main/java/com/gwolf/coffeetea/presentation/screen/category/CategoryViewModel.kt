package com.gwolf.coffeetea.presentation.screen.category

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Category
import com.gwolf.coffeetea.domain.usecase.database.get.GetCategoriesListUseCase
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryUiState(
    val categoriesList: List<Category> = listOf<Category>(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesListUseCase: GetCategoriesListUseCase
) : ViewModel() {

    private val _categoryScreenState = mutableStateOf(CategoryUiState())
    val categoryScreenState: State<CategoryUiState> = _categoryScreenState

    private suspend fun getCategories() {
        getCategoriesListUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _categoryScreenState.value =
                        _categoryScreenState.value.copy(
                            categoriesList = response.data,
                        )
                }

                is DataResult.Error -> {
                    _categoryScreenState.value =
                        _categoryScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }

    init {
        _categoryScreenState.value = _categoryScreenState.value.copy(isLoading = true)
        viewModelScope.launch {
            val categoriesList = async { getCategories() }

            try {
                awaitAll(categoriesList)
                _categoryScreenState.value = _categoryScreenState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading category screen data: ${e.message}")
            }
        }
    }

}