package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.dto.FavoriteDto
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun getFavorites(): Flow<UiResult<List<FavoriteDto>?>>
    suspend fun addFavorite(productId: Int): Flow<UiResult<Unit>>
    suspend fun removeFavorite(favoriteId: Int): Flow<UiResult<Unit>>
}