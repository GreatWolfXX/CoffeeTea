package com.gwolf.coffeetea.domain.model

data class Cart(
    val cartId: Int = -1,
    val productId: Int = -1,
    val quantity: Int = 0,
    val userId: String = "",
    val product: Product,
)