package com.gwolf.coffeetea.presentation.screen.favorite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Favorite
import com.gwolf.coffeetea.domain.usecase.database.get.GetFavoritesListUseCase
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

data class FavoriteScreenState(
    val favoritesList: List<Favorite> = listOf(),
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
)

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoritesListUseCase: GetFavoritesListUseCase
) : ViewModel() {

    private var _state = MutableStateFlow(FavoriteScreenState())
    val state: StateFlow<FavoriteScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = FavoriteScreenState()
    )

    private suspend fun getProducts() {
        getFavoritesListUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update { it.copy(favoritesList = response.data) }
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
            val productsList = async { getProducts() }

            try {
                awaitAll(productsList)
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading favorite screen data: ${e.message}")
            }
        }
    }
}