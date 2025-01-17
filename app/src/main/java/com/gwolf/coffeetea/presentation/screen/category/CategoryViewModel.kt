package com.gwolf.coffeetea.presentation.screen.category

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gwolf.coffeetea.domain.model.Category
import com.gwolf.coffeetea.domain.usecase.database.get.GetCategoriesListUseCase
import com.gwolf.coffeetea.util.LOGGER_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryUiState(
    val categoriesList: Flow<PagingData<Category>> = emptyFlow(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesListUseCase: GetCategoriesListUseCase
) : ViewModel() {

    private val _categoryScreenState = mutableStateOf(CategoryUiState())
    val categoryScreenState: State<CategoryUiState> = _categoryScreenState

    init {
        _categoryScreenState.value = _categoryScreenState.value.copy(isLoading = true)
        viewModelScope.launch {
            val categoriesList =  getCategoriesListUseCase.invoke().cachedIn(viewModelScope)
            try {
                _categoryScreenState.value = _categoryScreenState.value.copy(
                    categoriesList = categoriesList,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading category screen data: ${e.message}")
            }
        }
    }

}