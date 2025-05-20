package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.CartItemEntity
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItemProducts(): Flow<List<CartItemEntity>>
    fun addCartItem(productId: String, quantity: Int): Flow<String?>
    fun removeCartItem(cartId: String): Flow<Unit>
    fun updateCartItemProductQuantity(cartId: String, quantity: Int): Flow<Unit>
}