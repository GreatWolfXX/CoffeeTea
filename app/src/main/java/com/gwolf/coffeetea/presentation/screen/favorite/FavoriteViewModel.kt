package com.gwolf.coffeetea.presentation.screen.favorite

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.model.Favorite
import com.gwolf.coffeetea.domain.usecase.database.get.GetFavoritesListUseCase
import com.gwolf.coffeetea.util.UiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoriteUiState(
    val favoritesList: List<Favorite> = listOf<Favorite>(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoritesListUseCase: GetFavoritesListUseCase
) : ViewModel() {

    private val _favoriteScreenState = mutableStateOf(FavoriteUiState())
    val favoriteScreenState: State<FavoriteUiState> = _favoriteScreenState

    private suspend fun getProducts() {
        getFavoritesListUseCase.invoke().collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _favoriteScreenState.value =
                        _favoriteScreenState.value.copy(
                            favoritesList = response.data,
                        )
                }

                is UiResult.Error -> {
                    _favoriteScreenState.value =
                        _favoriteScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }

    init {
        _favoriteScreenState.value = _favoriteScreenState.value.copy(isLoading = true)
        viewModelScope.launch {
            val productsList = async { getProducts() }

            try {
                awaitAll(productsList)
                _favoriteScreenState.value = _favoriteScreenState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e("Coffee&TeaLogger", "Error loading favorite screen data: ${e.message}")
            }
        }
    }

}