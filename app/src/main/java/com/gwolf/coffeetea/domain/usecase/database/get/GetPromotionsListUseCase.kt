package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.model.Promotion
import com.gwolf.coffeetea.domain.repository.remote.supabase.PromotionRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.toDomain
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class GetPromotionsListUseCase @Inject constructor(
    private val promotionRepository: PromotionRepository,
    private val storage: Storage
) {
    operator fun invoke(): Flow<UiResult<List<Promotion>>> = callbackFlow {
        try {
            promotionRepository.getPromotions().collect { response ->
                val data = response.map { promotion ->
                    val imageUrl = storage.from(promotion.bucketId)
                        .createSignedUrl(promotion.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                    return@map promotion.toDomain(imageUrl)
                }
                trySend(UiResult.Success(data = data))
            }
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}