package com.gwolf.coffeetea.domain.usecase.database.add

import com.gwolf.coffeetea.domain.entities.Favorite
import com.gwolf.coffeetea.domain.repository.remote.supabase.FavoriteRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(productId: String): Flow<DataResult<Favorite>> = flow {
        try {
            favoriteRepository.addFavorite(productId).collect { response ->
                emit(DataResult.Success(data = response))
            }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}