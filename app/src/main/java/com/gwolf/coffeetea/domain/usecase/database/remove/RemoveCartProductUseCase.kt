package com.gwolf.coffeetea.domain.usecase.database.remove

import com.gwolf.coffeetea.domain.repository.remote.supabase.CartRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoveCartProductUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(cartId: String): Flow<DataResult<Unit>> = flow {
        try {
            cartRepository.removeCartItem(cartId).collect { response ->
                emit(DataResult.Success(data = response))
            }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}