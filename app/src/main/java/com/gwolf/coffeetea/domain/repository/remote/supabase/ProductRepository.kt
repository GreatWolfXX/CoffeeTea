package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.domain.entities.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    fun getProductById(productId: String): Flow<Product?>
    fun getProductsByCategory(categoryId: String): Flow<List<Product>>
    fun searchProducts(search: String): Flow<List<Product>>
    fun updateProductStockQuantity(productId: String, stockQuantity: Int): Flow<Unit>
    fun getMinAndMaxProductPriceByCategory(
        categoryId: String
    ): Flow<ClosedFloatingPointRange<Float>>

    fun getProductsByCategoryWithFilters(
        categoryId: String,
        isDescending: Boolean,
        priceRange: ClosedFloatingPointRange<Float>
    ): Flow<List<Product>>
}