package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.entities.CartEntity
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartProducts(): Flow<List<CartEntity>>
    fun addCart(productId: Int, quantity: Int): Flow<String?>
    fun removeCart(cartId: String): Flow<Unit>
}