package com.gwolf.coffeetea.presentation.screen.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Category
import com.gwolf.coffeetea.domain.usecase.database.get.GetCategoriesUseCase
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.LocalizedText
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
import timber.log.Timber
import javax.inject.Inject

data class CategoryScreenState(
    val categoriesList: List<Category> = listOf(),
    val isLoading: Boolean = false,
    val error: LocalizedText = LocalizedText.DynamicString(""),
)

sealed class CategoryEvent {
    data object Idle : CategoryEvent()
}

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesListUseCase: GetCategoriesUseCase
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
                    _state.update { it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty())) }
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
                Timber.d("Error loading category screen data: ${e.message}")
            }
        }
    }
}