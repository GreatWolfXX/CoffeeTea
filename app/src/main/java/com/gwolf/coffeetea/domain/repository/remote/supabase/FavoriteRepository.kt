package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.FavoriteEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavorites(): Flow<List<FavoriteEntity>>
    fun addFavorite(productId: String): Flow<FavoriteEntity>
    fun removeFavorite(favoriteId: String): Flow<Unit>
}