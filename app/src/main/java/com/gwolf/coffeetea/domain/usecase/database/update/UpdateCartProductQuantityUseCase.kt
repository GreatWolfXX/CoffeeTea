package com.gwolf.coffeetea.domain.usecase.database.update

import com.gwolf.coffeetea.domain.repository.remote.CartRepository
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UpdateCartProductQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(cartId: String, quantity: Int): Flow<UiResult<Unit>> = callbackFlow {
        try {
            cartRepository.updateCartProductQuantity(cartId, quantity)
                .collect { response ->
                    trySend(UiResult.Success(data = response))
                }
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}