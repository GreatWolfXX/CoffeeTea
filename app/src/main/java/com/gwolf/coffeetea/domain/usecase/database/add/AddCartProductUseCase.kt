package com.gwolf.coffeetea.domain.usecase.database.add

import com.gwolf.coffeetea.domain.repository.remote.supabase.CartRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddCartProductUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(productId: String, quantity: Int): Flow<DataResult<String>> = flow {
        try {
            cartRepository.addCartItem(productId, quantity).collect { response ->
                if (response != null) {
                    emit(DataResult.Success(data = response))
                } else {
                    emit(DataResult.Error(exception = Exception("Add item to cart failed!")))
                }
            }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}