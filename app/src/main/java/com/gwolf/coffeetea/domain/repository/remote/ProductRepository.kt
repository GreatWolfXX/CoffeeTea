package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.dto.ProductDto
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts(): Flow<List<ProductDto>>
    suspend fun getProductById(productId: Int): Flow<ProductDto?>
    suspend fun getProductsByCategory(categoryId: Int): Flow<List<ProductDto>>
    suspend fun searchProducts(search: String): Flow<List<ProductDto>>
}