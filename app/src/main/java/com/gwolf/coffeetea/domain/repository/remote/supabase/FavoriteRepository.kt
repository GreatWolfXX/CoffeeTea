package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.data.entities.supabase.FavoriteEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavorites(): Flow<List<FavoriteEntity>>
    fun addFavorite(productId: Int): Flow<Unit>
    fun removeFavorite(favoriteId: String): Flow<Unit>
}