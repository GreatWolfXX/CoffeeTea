package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.domain.entities.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItemProducts(): Flow<List<CartItem>>
    fun addCartItem(productId: String, quantity: Int): Flow<String?>
    fun removeCartItem(cartId: String): Flow<Unit>
    fun updateCartItemProductQuantity(cartId: String, quantity: Int): Flow<Unit>
}