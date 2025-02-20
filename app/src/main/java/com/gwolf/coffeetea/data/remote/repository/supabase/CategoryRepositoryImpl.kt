package com.gwolf.coffeetea.data.remote.repository.supabase

import com.gwolf.coffeetea.data.entities.supabase.CategoryEntity
import com.gwolf.coffeetea.domain.repository.remote.supabase.CategoryRepository
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
    override fun getCategories(): Flow<List<CategoryEntity>> = callbackFlow {
        val response = withContext(Dispatchers.IO) {
            postgrest.from(CATEGORIES_TABLE)
                .select()
                .decodeList<CategoryEntity>()
        }
        trySend(response)
        close()
        awaitClose()
    }
}