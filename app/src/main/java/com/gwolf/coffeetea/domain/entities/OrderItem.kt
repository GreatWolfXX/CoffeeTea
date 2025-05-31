package com.gwolf.coffeetea.domain.entities

data class OrderItem(
    val id: String = "",
    val orderId: String,
    val productId: String,
    val product: Product,
    val quantity: Int,
)