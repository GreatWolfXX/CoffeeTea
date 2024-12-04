package com.gwolf.coffeetea.domain.usecase.database.add

import com.gwolf.coffeetea.domain.repository.remote.FavoriteRepository
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(productId: Int): Flow<UiResult<Unit>> = callbackFlow {
        favoriteRepository.addFavorite(productId).collect { result ->
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