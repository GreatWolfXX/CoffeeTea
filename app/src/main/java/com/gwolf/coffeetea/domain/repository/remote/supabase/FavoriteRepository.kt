package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.domain.entities.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavorites(): Flow<List<Favorite>>
    fun addFavorite(productId: String): Flow<Favorite>
    fun removeFavorite(favoriteId: String): Flow<Unit>
}