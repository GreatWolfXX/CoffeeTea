package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.PromotionEntity
import kotlinx.coroutines.flow.Flow

interface PromotionRepository {
    fun getPromotions(): Flow<List<PromotionEntity>>
}