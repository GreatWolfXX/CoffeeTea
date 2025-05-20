package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.data.toDomain
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.domain.repository.remote.supabase.ProductRepository
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class GetProductsByCategoryUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val storage: Storage
) {
    operator fun invoke(categoryId: String): Flow<DataResult<List<Product>>> = callbackFlow {
        try {
            productRepository.getProductsByCategory(categoryId).collect { response ->
                val data = response.map { product ->
                    val imageUrl = storage.from(product.bucketId)
                        .createSignedUrl(product.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                    return@map product.toDomain(imageUrl)
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