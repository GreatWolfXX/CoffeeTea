package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.entities.Category
import com.gwolf.coffeetea.domain.repository.remote.supabase.CategoryRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.domain.toDomain
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class GetCategoriesListUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val storage: Storage
) {
    operator fun invoke(): Flow<DataResult<List<Category>>> = callbackFlow {
        try {
            categoryRepository.getCategories().collect { response ->
                val data = response.map { category ->
                    val imageUrl = storage.from(category.bucketId)
                        .createSignedUrl(category.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                    return@map category.toDomain(imageUrl)
                }
                trySend(DataResult.Success(data = data))
            }
        } catch (e: Exception) {
            trySend(DataResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}