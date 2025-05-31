package com.gwolf.coffeetea.data.dto.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    @SerialName("id") val id: String = "",
    @SerialName("order_number") val orderNumber: Int = -1,
    @SerialName("user_id") val userId: String,
    @SerialName("address_id") val addressId: String,
    @SerialName("delivery_addresses") val address: AddressDto? = null,
    @SerialName("total_price") val totalPrice: Double,
    @SerialName("order_items") val orderItems: List<OrderItemDto> = emptyList(),
    @SerialName("created_at") val createdAt: String = ""
)