package com.gwolf.coffeetea.data.dto.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartEntity(
    @SerialName("cart_id") val id: String = "",
    @SerialName("product_id") val productId: Int,
    @SerialName("quantity") val quantity: Int,
    @SerialName("user_id") val userId: String,
    @SerialName("products") val product: ProductEntity? = null,
)