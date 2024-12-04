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
    operator fun invoke(productId: Int, quantity: Int): Flow<UiResult<Unit>> = callbackFlow {
        cartRepository.addCart(productId, quantity).collect { result ->
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