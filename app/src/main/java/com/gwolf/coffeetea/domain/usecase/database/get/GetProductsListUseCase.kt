package com.gwolf.coffeetea.domain.usecase.database.get

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

class GetProductsListUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val storage: Storage
) {
    operator fun invoke(): Flow<UiResult<List<Product>?>> = callbackFlow {
        productRepository.getProducts().collect { result ->
            when(result) {
                is UiResult.Success -> {
                    val data = result.data?.map { product ->
                        //Warning, maybe execute exception?
                        val imageUrl = storage.from(product.bucketId).createSignedUrl(product.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                        return@map product.toDomain(imageUrl)
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