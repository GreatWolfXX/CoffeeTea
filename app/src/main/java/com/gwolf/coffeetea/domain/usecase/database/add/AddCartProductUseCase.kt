package com.gwolf.coffeetea.domain.usecase.database.add

import com.gwolf.coffeetea.domain.repository.remote.CartRepository
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AddCartProductUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(productId: Int, quantity: Int): Flow<UiResult<String>> = callbackFlow {
        try {
            cartRepository.addCart(productId, quantity).collect { response ->
                if (response != null) {
                    trySend(UiResult.Success(data = response))
                } else {
                    trySend(UiResult.Error(exception = Exception("Add item to cart failed!")))
                }
            }
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}