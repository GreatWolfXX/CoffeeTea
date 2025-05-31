package com.gwolf.coffeetea.domain.entities

data class Order(
    val id: String = "",
    val orderNumber: Int,
    val userId: String,
    val address: Address,
    val totalPrice: Double,
    val createdAt: String,
    val orderItems: List<OrderItem>
)