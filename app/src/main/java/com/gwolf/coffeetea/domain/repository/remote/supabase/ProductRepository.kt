package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.ProductEntity
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<ProductEntity>>
    fun getProductById(productId: String): Flow<ProductEntity?>
    fun getProductsByCategory(categoryId: String): Flow<List<ProductEntity>>
    fun searchProducts(search: String): Flow<List<ProductEntity>>
    fun getMinAndMaxProductPriceByCategory(categoryId: String): Flow<ClosedFloatingPointRange<Float>>
    fun getProductsByCategoryWithFilters(
        categoryId: String,
        isDescending: Boolean,
        priceRange: ClosedFloatingPointRange<Float>
    ): Flow<List<ProductEntity>>
}