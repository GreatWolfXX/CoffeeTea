package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.domain.repository.remote.supabase.ProductRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProductsByCategoryWithFiltersUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(
        categoryId: String,
        isDescending: Boolean,
        priceRange: ClosedFloatingPointRange<Float>
    ): Flow<DataResult<List<Product>>> = flow {
        try {
            productRepository.getProductsByCategoryWithFilters(categoryId, isDescending, priceRange)
                .collect { response ->
                    emit(DataResult.Success(data = response))
                }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}