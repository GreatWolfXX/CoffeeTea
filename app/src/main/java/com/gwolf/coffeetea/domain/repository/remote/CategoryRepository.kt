package com.gwolf.coffeetea.domain.repository.remote

import androidx.paging.PagingData
import com.gwolf.coffeetea.data.local.database.entities.LocalCategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<PagingData<LocalCategoryEntity>>
}