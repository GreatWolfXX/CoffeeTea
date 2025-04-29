package com.gwolf.coffeetea.domain.usecase.database.remove

import com.gwolf.coffeetea.domain.repository.remote.supabase.CartRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RemoveCartProductUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(cartId: String): Flow<DataResult<Unit>> = callbackFlow {
        try {
            cartRepository.removeCart(cartId).collect { response ->
                trySend(DataResult.Success(data = response))
            }
        } catch (e: Exception) {
            trySend(DataResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}