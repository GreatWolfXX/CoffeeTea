package com.gwolf.coffeetea.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartDto(
    @SerialName("cart_id") val cartId: Int = -1,
    @SerialName("product_id") val productId: Int = -1,
    @SerialName("quantity") val quantity: Int = 0,
    @SerialName("user_id") val userId: String = "",
    @SerialName("products") val product: ProductDto? = null,
)