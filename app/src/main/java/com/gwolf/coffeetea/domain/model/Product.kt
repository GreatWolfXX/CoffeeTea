package com.gwolf.coffeetea.domain.model

data class Product(
    val id: Int = -1,
    val name: String = "",
    val amount: Double = 0.0,
    val unit: String = "",
    val featuresDescription: String = "",
    val fullDescription: String = "",
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val imageUrl: String = "",

    val categoryName: String = "",
    val favoriteId: Int = -1,
    val cartId: Int = -1,
)