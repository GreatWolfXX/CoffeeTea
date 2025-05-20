package com.gwolf.coffeetea.domain.entities

data class CartItem(
    val id: String,
    val product: Product,
    val quantity: Int,
)