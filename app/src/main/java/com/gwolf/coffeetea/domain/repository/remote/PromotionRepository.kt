package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.dto.PromotionDto
import kotlinx.coroutines.flow.Flow

interface PromotionRepository {
    suspend fun getPromotions(): Flow<List<PromotionDto>>
}