package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategories(): Flow<List<CategoryEntity>>
}