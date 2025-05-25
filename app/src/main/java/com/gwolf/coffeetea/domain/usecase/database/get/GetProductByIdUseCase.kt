package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.domain.repository.remote.supabase.ProductRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(productId: String): Flow<DataResult<Product>> = flow {
        try {
            productRepository.getProductById(productId).collect { response ->
                if (response != null) {
                    emit(DataResult.Success(data = response))
                } else {
                    emit(DataResult.Error(exception = Exception("Failed to find item by id!")))
                }
            }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}