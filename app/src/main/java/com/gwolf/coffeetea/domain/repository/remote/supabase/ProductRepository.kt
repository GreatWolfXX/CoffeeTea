package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.ProductEntity
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<ProductEntity>>
    fun getProductById(productId: Int): Flow<ProductEntity?>
    fun getProductsByCategory(categoryId: Int): Flow<List<ProductEntity>>
    fun searchProducts(search: String): Flow<List<ProductEntity>>
}