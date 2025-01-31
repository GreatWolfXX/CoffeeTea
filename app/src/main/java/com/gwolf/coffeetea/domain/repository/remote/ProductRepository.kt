package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<ProductEntity>>
    fun getProductById(productId: Int): Flow<ProductEntity?>
    fun getProductsByCategory(categoryId: Int): Flow<List<ProductEntity>>
    fun searchProducts(search: String): Flow<List<ProductEntity>>
}