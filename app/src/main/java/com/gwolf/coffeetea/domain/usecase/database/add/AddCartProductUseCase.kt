package com.gwolf.coffeetea.domain.usecase.database.add

import com.gwolf.coffeetea.domain.repository.remote.supabase.CartRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AddCartProductUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(productId: String, quantity: Int): Flow<DataResult<String>> = callbackFlow {
        try {
            cartRepository.addCartItem(productId, quantity).collect { response ->
                if (response != null) {
                    trySend(DataResult.Success(data = response))
                } else {
                    trySend(DataResult.Error(exception = Exception("Add item to cart failed!")))
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