package com.gwolf.coffeetea.data.dto.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemDto(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("quantity") val quantity: Int,

    @SerialName("products") val product: ProductDto? = null,
)