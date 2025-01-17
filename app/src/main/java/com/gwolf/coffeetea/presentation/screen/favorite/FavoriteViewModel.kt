package com.gwolf.coffeetea.presentation.screen.favorite

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gwolf.coffeetea.domain.model.Favorite
import com.gwolf.coffeetea.domain.usecase.database.get.GetFavoritesListUseCase
import com.gwolf.coffeetea.util.LOGGER_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoriteUiState(
    val favoritesList: Flow<PagingData<Favorite>> = emptyFlow(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoritesListUseCase: GetFavoritesListUseCase
) : ViewModel() {

    private val _favoriteScreenState = mutableStateOf(FavoriteUiState())
    val favoriteScreenState: State<FavoriteUiState> = _favoriteScreenState

    init {
        _favoriteScreenState.value = _favoriteScreenState.value.copy(isLoading = true)
        viewModelScope.launch {
            val favoritesList =  getFavoritesListUseCase.invoke().cachedIn(viewModelScope)
            try {
                _favoriteScreenState.value = _favoriteScreenState.value.copy(
                    favoritesList = favoritesList,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading favorite screen data: ${e.message}")
            }
        }
    }

}