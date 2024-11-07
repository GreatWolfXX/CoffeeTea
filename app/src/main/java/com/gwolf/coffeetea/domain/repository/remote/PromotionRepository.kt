package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.dto.PromotionDto
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.flow.Flow

interface PromotionRepository {
    suspend fun getPromotions(): Flow<UiResult<List<PromotionDto>?>>
}