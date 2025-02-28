package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.model.Cart
import com.gwolf.coffeetea.domain.repository.remote.supabase.CartRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.toDomain
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
    operator fun invoke(): Flow<UiResult<List<Cart>>> = callbackFlow {
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
                trySend(UiResult.Success(data = data))
            }
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}