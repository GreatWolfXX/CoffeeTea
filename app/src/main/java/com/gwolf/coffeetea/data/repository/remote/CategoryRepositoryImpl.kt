package com.gwolf.coffeetea.data.repository.remote

import com.gwolf.coffeetea.data.dto.CategoryDto
import com.gwolf.coffeetea.domain.repository.remote.CategoryRepository
import com.gwolf.coffeetea.util.CATEGORIES_TABLE
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : CategoryRepository {
    override suspend fun getCategories(): Flow<List<CategoryDto>> = callbackFlow {
        val response = withContext(Dispatchers.IO) {
            postgrest.from(CATEGORIES_TABLE)
                .select()
                .decodeList<CategoryDto>()
        }
        trySend(response)
        awaitClose()
    }
}