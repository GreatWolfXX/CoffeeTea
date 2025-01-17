package com.gwolf.coffeetea.domain.usecase.database.get

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.gwolf.coffeetea.domain.model.Promotion
import com.gwolf.coffeetea.domain.repository.remote.PromotionRepository
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.toDomain
import com.gwolf.coffeetea.util.toEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GetPromotionsListUseCase @Inject constructor(
    private val promotionRepository: PromotionRepository
) {
    operator fun invoke(): Flow<PagingData<Promotion>> = callbackFlow {
        try {
            promotionRepository.getPromotions().collect { response ->
                val data = response.map { promotionPagingData ->
                    val promotion = promotionPagingData.toEntity()
                    Log.d(LOGGER_TAG, "Image path: ${promotionPagingData.imagePath}")
                    return@map promotion.toDomain(promotionPagingData.imageUrl)
                }
                trySend(data)
            }
        } catch (e: Exception) {
            Log.d(LOGGER_TAG, "Promotion Paging Data Error! : $e")
        } finally {
            close()
        }
        awaitClose()
    }
}