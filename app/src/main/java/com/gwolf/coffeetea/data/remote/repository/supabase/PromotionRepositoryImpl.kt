package com.gwolf.coffeetea.data.remote.repository.supabase

import com.gwolf.coffeetea.data.entities.supabase.PromotionEntity
import com.gwolf.coffeetea.domain.repository.remote.supabase.PromotionRepository
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
    override fun getPromotions(): Flow<List<PromotionEntity>> = callbackFlow {
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PROMOTIONS_TABLE)
                .select()
                .decodeList<PromotionEntity>()
        }
        trySend(response)
        close()
        awaitClose()
    }
}