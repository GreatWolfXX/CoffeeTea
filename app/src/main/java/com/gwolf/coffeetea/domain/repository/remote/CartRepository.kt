package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.dto.CartDto
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun getCartProducts(): Flow<List<CartDto>>
    suspend fun addCart(productId: Int, quantity: Int): Flow<Int?>
    suspend fun removeCart(cartId: Int): Flow<Unit>
}