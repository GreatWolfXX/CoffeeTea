package com.gwolf.coffeetea.domain.usecase.database.remove

import com.gwolf.coffeetea.domain.repository.remote.supabase.FavoriteRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoveFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(favoriteId: String): Flow<DataResult<Unit>> = flow {
        try {
            favoriteRepository.removeFavorite(favoriteId).collect { response ->
                emit(DataResult.Success(data = response))
            }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}