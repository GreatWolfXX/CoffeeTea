package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.dto.FavoriteDto
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun getFavorites(): Flow<List<FavoriteDto>>
    suspend fun addFavorite(productId: Int): Flow<Unit>
    suspend fun removeFavorite(favoriteId: Int): Flow<Unit>
}