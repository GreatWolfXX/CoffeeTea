package com.gwolf.coffeetea.data.repository.remote

import com.gwolf.coffeetea.data.dto.CategoryDto
import com.gwolf.coffeetea.domain.repository.remote.CategoryRepository
import com.gwolf.coffeetea.util.CATEGORIES_TABLE
import com.gwolf.coffeetea.util.UiResult
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
    override suspend fun getCategories(): Flow<UiResult<List<CategoryDto>?>> = callbackFlow {
        try {
            val response = withContext(Dispatchers.IO) {
                postgrest.from(CATEGORIES_TABLE)
                    .select()
                    .decodeList<CategoryDto>()
            }
            trySend(UiResult.Success(response))
            close()
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
            close()
        }
        awaitClose()
    }
}