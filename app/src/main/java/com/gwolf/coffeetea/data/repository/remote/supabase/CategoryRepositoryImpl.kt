package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.CategoryDto
import com.gwolf.coffeetea.data.toDomain
import com.gwolf.coffeetea.domain.entities.Category
import com.gwolf.coffeetea.domain.repository.remote.supabase.CategoryRepository
import com.gwolf.coffeetea.util.CATEGORIES_TABLE
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class CategoryRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage
) : CategoryRepository {
    override fun getCategories(): Flow<List<Category>> = flow {
        val response = withContext(Dispatchers.IO) {
            postgrest.from(CATEGORIES_TABLE)
                .select()
                .decodeList<CategoryDto>()
        }

        val data = response.map { category ->
            val imageUrl = storage.from(category.bucketId)
                .createSignedUrl(category.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
            category.toDomain(imageUrl)
        }

        emit(data)
    }
}