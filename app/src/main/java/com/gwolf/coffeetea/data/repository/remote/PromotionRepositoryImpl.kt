package com.gwolf.coffeetea.data.repository.remote

import com.gwolf.coffeetea.data.dto.PromotionDto
import com.gwolf.coffeetea.domain.repository.remote.PromotionRepository
import com.gwolf.coffeetea.util.PROMOTIONS_TABLE
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PromotionRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : PromotionRepository {
    override suspend fun getPromotions(): Flow<List<PromotionDto>> = callbackFlow {
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PROMOTIONS_TABLE)
                .select()
                .decodeList<PromotionDto>()
        }
        trySend(response)
        awaitClose()
    }
}