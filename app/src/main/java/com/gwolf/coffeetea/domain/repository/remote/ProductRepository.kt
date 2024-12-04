package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.dto.ProductDto
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts(): Flow<UiResult<List<ProductDto>?>>
    suspend fun getProductById(productId: Int): Flow<UiResult<ProductDto?>>
    suspend fun getProductsByCategory(categoryId: Int): Flow<UiResult<List<ProductDto>?>>
    suspend fun searchProducts(search: String): Flow<UiResult<List<ProductDto>?>>
}