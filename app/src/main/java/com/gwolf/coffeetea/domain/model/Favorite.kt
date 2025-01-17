package com.gwolf.coffeetea.domain.model

data class Favorite(
    val id: String,
    val productId: Int,
    val product: Product
)