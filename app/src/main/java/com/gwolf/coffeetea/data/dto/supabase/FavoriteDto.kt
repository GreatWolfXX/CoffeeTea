package com.gwolf.coffeetea.data.dto.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,

    @SerialName("products") val product: ProductDto? = null
)