package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.domain.entities.CartItem
import com.gwolf.coffeetea.domain.entities.Order
import kotlinx.coroutines.flow.Flow

interface OrdersRepository {
    fun getOrders(): Flow<List<Order>>

    fun addOrder(
        totalPrice: Double,
        addressId: String
    ): Flow<Order>

    fun addOrderItem(
        orderId: String,
        listCartItems: List<CartItem>
    ): Flow<Unit>

    fun removeOrder(
        orderId: String,
    ): Flow<Unit>
}