package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.model.Category
import com.gwolf.coffeetea.domain.repository.remote.CategoryRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.toDomain
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
    operator fun invoke(): Flow<UiResult<List<Category>?>> = callbackFlow {
        categoryRepository.getCategories().collect { result ->
            when(result) {
                is UiResult.Success -> {
                    val data = result.data?.map { category ->
                        val imageUrl = storage.from(category.bucketId).createSignedUrl(category.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                        return@map category.toDomain(imageUrl)
                    }
                    trySend(UiResult.Success(data = data))
                    close()
                }
                is UiResult.Error -> {
                    trySend(result)
                    close()
                }
            }
        }
        awaitClose()
    }
}