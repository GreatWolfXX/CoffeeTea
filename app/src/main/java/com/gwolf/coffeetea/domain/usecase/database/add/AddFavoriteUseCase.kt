package com.gwolf.coffeetea.domain.usecase.database.add

import com.gwolf.coffeetea.data.dto.supabase.FavoriteEntity
import com.gwolf.coffeetea.domain.repository.remote.supabase.FavoriteRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(productId: String): Flow<DataResult<FavoriteEntity>> = callbackFlow {
        try {
            favoriteRepository.addFavorite(productId).collect { response ->
                trySend(DataResult.Success(data = response))
            }
        } catch (e: Exception) {
            trySend(DataResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}