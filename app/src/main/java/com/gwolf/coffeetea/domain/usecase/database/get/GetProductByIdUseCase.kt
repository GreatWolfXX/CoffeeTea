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

class GetProductByIdUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val storage: Storage
) {
    operator fun invoke(productId: String): Flow<DataResult<Product>> = callbackFlow {
        try {
            productRepository.getProductById(productId).collect { response ->
                if (response != null) {
                    val imageUrl = storage.from(response.bucketId)
                        .createSignedUrl(response.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                    trySend(DataResult.Success(data = response.toDomain(imageUrl)))
                } else {
                    trySend(DataResult.Error(exception = Exception("Failed to find item by id!")))
                }
            }
        } catch (e: Exception) {
            trySend(DataResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}