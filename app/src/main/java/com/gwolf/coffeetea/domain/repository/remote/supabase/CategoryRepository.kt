package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.domain.entities.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
}