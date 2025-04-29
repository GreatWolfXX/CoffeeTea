package com.gwolf.coffeetea.domain.entities

data class Favorite(
    val id: String,
    val productId: Int,
    val product: Product
)