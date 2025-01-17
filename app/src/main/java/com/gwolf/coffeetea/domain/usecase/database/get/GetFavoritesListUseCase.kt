package com.gwolf.coffeetea.domain.usecase.database.get

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.gwolf.coffeetea.domain.model.Favorite
import com.gwolf.coffeetea.domain.repository.remote.FavoriteRepository
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.toDomain
import com.gwolf.coffeetea.util.toEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GetFavoritesListUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(): Flow<PagingData<Favorite>> = callbackFlow {
        try {
            favoriteRepository.getFavorites().collect { response ->
                val data = response.map { favoritePagingData ->
                    val favorite = favoritePagingData.toEntity()
                    return@map favorite.toDomain(favoritePagingData.product.imageUrl)
                }
                trySend(data)
            }
        } catch (e: Exception) {
            Log.d(LOGGER_TAG, "Favorite Paging Data Error! : $e")
        } finally {
            close()
        }
        awaitClose()
    }
}