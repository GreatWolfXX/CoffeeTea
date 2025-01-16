package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.entities.FavoriteEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun getFavorites(): Flow<List<FavoriteEntity>>
    suspend fun addFavorite(productId: Int): Flow<Unit>
    suspend fun removeFavorite(favoriteId: Int): Flow<Unit>
}