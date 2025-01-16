package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.dto.CategoryDto
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategories(): Flow<List<CategoryDto>>
}