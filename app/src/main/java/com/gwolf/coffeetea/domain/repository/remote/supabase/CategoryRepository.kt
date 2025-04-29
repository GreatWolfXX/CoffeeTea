package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<CategoryEntity>>
}