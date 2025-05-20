package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.data.toDomain
import com.gwolf.coffeetea.domain.entities.CartItem
import com.gwolf.coffeetea.domain.repository.remote.supabase.CartRepository
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class GetCartProductsUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val storage: Storage
) {
    operator fun invoke(): Flow<DataResult<List<CartItem>>> = callbackFlow {
        try {
            cartRepository.getCartItemProducts().collect { response ->
                val data = response.map { cartItem ->
                    val productImageUrl = storage.from(cartItem.product?.bucketId.orEmpty())
                        .createSignedUrl(
                            cartItem.product?.imagePath.orEmpty(),
                            HOURS_EXPIRES_IMAGE_URL.hours
                        )
                    return@map cartItem.toDomain(productImageUrl)
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