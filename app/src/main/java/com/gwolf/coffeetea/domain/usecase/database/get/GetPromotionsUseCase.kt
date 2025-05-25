package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.entities.Promotion
import com.gwolf.coffeetea.domain.repository.remote.supabase.PromotionRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPromotionsUseCase @Inject constructor(
    private val promotionRepository: PromotionRepository
) {
    operator fun invoke(): Flow<DataResult<List<Promotion>>> = flow {
        try {
            promotionRepository.getPromotions().collect { response ->
                emit(DataResult.Success(data = response))
            }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}