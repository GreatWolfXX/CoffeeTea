package com.gwolf.coffeetea.domain.repository.remote

import androidx.paging.PagingData
import com.gwolf.coffeetea.data.local.database.entities.LocalPromotionEntity
import kotlinx.coroutines.flow.Flow

interface PromotionRepository {
    fun getPromotions(): Flow<PagingData<LocalPromotionEntity>>
}