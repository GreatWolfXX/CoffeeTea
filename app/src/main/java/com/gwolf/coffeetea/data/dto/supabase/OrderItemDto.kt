package com.gwolf.coffeetea.data.dto.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItemDto(
    @SerialName("id") val id: String = "",
    @SerialName("order_id") val orderId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("products") val product: ProductDto? = null,
    @SerialName("quantity") val quantity: Int,
)