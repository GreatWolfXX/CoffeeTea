package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.entities.Cart
import com.gwolf.coffeetea.domain.repository.remote.supabase.CartRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.domain.toDomain
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class GetCartProductsListUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val storage: Storage
) {
    operator fun invoke(): Flow<DataResult<List<Cart>>> = callbackFlow {
        try {
            cartRepository.getCartProducts().collect { response ->
                val data = response.map { cartProduct ->
                    val productImageUrl = storage.from(cartProduct.product?.bucketId.orEmpty())
                        .createSignedUrl(
                            cartProduct.product?.imagePath.orEmpty(),
                            HOURS_EXPIRES_IMAGE_URL.hours
                        )
                    return@map cartProduct.toDomain(productImageUrl)
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