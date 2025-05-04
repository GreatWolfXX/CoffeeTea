package com.gwolf.coffeetea.presentation.screen.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Category
import com.gwolf.coffeetea.domain.usecase.database.get.GetCategoriesListUseCase
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryScreenState(
    val categoriesList: List<Category> = listOf(),
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
)

sealed class CategoryEvent {
    data object Idle : CategoryEvent()
}

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesListUseCase: GetCategoriesListUseCase
) : ViewModel() {

    private var _state = MutableStateFlow(CategoryScreenState())
    val state: StateFlow<CategoryScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = CategoryScreenState()
    )

    private var _event: Channel<CategoryEvent> = Channel()
    val event = _event.receiveAsFlow()

    private suspend fun getCategories() {
        getCategoriesListUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update { it.copy(categoriesList = response.data) }
                }

                is DataResult.Error -> {
                    _state.update { it.copy(error = UiText.DynamicString(response.exception.message.orEmpty())) }
                }
            }
        }
    }

    init {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val categoriesList = async { getCategories() }

            try {
                awaitAll(categoriesList)
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading category screen data: ${e.message}")
            }
        }
    }
}