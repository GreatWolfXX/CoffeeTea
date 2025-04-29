package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.CartEntity
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartProducts(): Flow<List<CartEntity>>
    fun addCart(productId: Int, quantity: Int): Flow<String?>
    fun removeCart(cartId: String): Flow<Unit>
    fun updateCartProductQuantity(cartId: String, quantity: Int): Flow<Unit>
}