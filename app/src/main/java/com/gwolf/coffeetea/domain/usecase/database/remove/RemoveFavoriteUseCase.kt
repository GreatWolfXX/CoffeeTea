package com.gwolf.coffeetea.domain.usecase.database.remove

import com.gwolf.coffeetea.domain.repository.remote.FavoriteRepository
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RemoveFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(favoriteId: Int): Flow<UiResult<Unit>> = callbackFlow {
        favoriteRepository.removeFavorite(favoriteId).collect { result ->
            when(result) {
                is UiResult.Success -> {
                    trySend(UiResult.Success(data = Unit))
                    close()
                }
                is UiResult.Error -> {
                    trySend(result)
                    close()
                }
            }
        }
        awaitClose()
    }
}