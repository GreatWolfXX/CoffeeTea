package com.gwolf.coffeetea.domain.repository.remote

import androidx.paging.PagingData
import com.gwolf.coffeetea.data.local.database.entities.FavoriteWithProductEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavorites(): Flow<PagingData<FavoriteWithProductEntity>>
    fun addFavorite(productId: Int): Flow<Unit>
    fun removeFavorite(favoriteId: String): Flow<Unit>
}