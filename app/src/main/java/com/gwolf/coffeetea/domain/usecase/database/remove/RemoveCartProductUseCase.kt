package com.gwolf.coffeetea.domain.usecase.database.remove

import com.gwolf.coffeetea.domain.repository.remote.CartRepository
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RemoveCartProductUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(productId: Int): Flow<UiResult<Unit>> = callbackFlow {
        cartRepository.removeCart(productId).collect { result ->
            when(result) {
                is UiResult.Success -> {
                    trySend(UiResult.Success(data = Unit))
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