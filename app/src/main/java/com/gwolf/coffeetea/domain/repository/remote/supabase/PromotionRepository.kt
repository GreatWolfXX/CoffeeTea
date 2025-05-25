package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.domain.entities.Promotion
import kotlinx.coroutines.flow.Flow

interface PromotionRepository {
    fun getPromotions(): Flow<List<Promotion>>
}