package com.gwolf.coffeetea.domain.usecase.database

import com.gwolf.coffeetea.domain.model.Product
import com.gwolf.coffeetea.domain.repository.remote.ProductRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.toDomain
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
    operator fun invoke(productId: Int): Flow<UiResult<Product?>> = callbackFlow {
        productRepository.getProductById(productId).collect { result ->
            when(result) {
                is UiResult.Success -> {
                    val data = result.data!!
                    val imageUrl = storage.from(data.bucketId).createSignedUrl(data.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                    trySend(UiResult.Success(data = data.toDomain(imageUrl)))
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