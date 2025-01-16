package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts(): Flow<List<ProductEntity>>
    suspend fun getProductById(productId: Int): Flow<ProductEntity?>
    suspend fun getProductsByCategory(categoryId: Int): Flow<List<ProductEntity>>
    suspend fun searchProducts(search: String): Flow<List<ProductEntity>>
}