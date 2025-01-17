package com.gwolf.coffeetea.domain.model

data class Product(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val featuresDescription: String,
    val fullDescription: String,
    val price: Double,
    val rating: Double,
    val imageUrl: String,
    val categoryName: String,
    val favoriteId: String,
    val cartId: String,
)