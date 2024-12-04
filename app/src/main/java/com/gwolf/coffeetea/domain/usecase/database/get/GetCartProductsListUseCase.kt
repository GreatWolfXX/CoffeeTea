package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.model.Cart
import com.gwolf.coffeetea.domain.repository.remote.CartRepository
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
    operator fun invoke(): Flow<UiResult<List<Cart>?>> = callbackFlow {
        cartRepository.getCartProducts().collect { result ->
            when(result) {
                is UiResult.Success -> {
                    val data = result.data?.map { cartProduct ->
                        //Warning, maybe execute exception?
                        val productImageUrl = storage.from(cartProduct.product?.bucketId!!).createSignedUrl(cartProduct.product.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                        return@map cartProduct.toDomain(productImageUrl)
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