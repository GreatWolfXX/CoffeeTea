package com.gwolf.coffeetea.domain.repository.remote

import androidx.paging.PagingData
import com.gwolf.coffeetea.data.entities.ProductEntity
import com.gwolf.coffeetea.data.local.database.entities.LocalProductEntity
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<PagingData<LocalProductEntity>>
    fun getProductById(productId: Int): Flow<ProductEntity?>
    fun getProductsByCategory(categoryId: Int): Flow<List<ProductEntity>>
    fun searchProducts(search: String): Flow<List<ProductEntity>>
}