package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.dto.CategoryDto
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategories(): Flow<UiResult<List<CategoryDto>?>>
}