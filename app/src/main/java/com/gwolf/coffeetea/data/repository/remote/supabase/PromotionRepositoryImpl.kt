package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.PromotionDto
import com.gwolf.coffeetea.data.toDomain
import com.gwolf.coffeetea.domain.entities.Promotion
import com.gwolf.coffeetea.domain.repository.remote.supabase.PromotionRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.PROMOTIONS_TABLE
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class PromotionRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage
) : PromotionRepository {
    override fun getPromotions(): Flow<List<Promotion>> = flow {
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PROMOTIONS_TABLE)
                .select()
                .decodeList<PromotionDto>()
        }

        val data = response.map { promotion ->
            val imageUrl = storage.from(promotion.bucketId)
                .createSignedUrl(promotion.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
            return@map promotion.toDomain(imageUrl)
        }

        emit(data)
    }
}