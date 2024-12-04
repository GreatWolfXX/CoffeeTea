package com.gwolf.coffeetea.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    @SerialName("favorite_id") val favoriteId: Int = -1,
    @SerialName("product_id") val productId: Int = -1,
    @SerialName("user_id") val userId: String = "",
    @SerialName("products") val product: ProductDto? = null,
)