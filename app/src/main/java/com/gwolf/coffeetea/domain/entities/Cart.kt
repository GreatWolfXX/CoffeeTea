package com.gwolf.coffeetea.domain.entities

data class Cart(
    val cartId: String,
    val productId: Int,
    val quantity: Int,
    val product: Product,
)