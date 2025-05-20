package com.gwolf.coffeetea.domain.entities

data class Product(
    val id: String,
    val categoryName: String,
    val name: String,
    val stockQuantity: Int,
    val amount: String,
    val unit: String,
    val featuresDescription: String,
    val fullDescription: String,
    val price: Double,
    val rating: Double,
    val imageUrl: String,
    val favoriteId: String,
    val cartItemId: String
)