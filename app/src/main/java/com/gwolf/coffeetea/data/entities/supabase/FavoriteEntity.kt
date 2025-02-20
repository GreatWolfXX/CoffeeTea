package com.gwolf.coffeetea.data.entities.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteEntity(
    @SerialName("favorite_id") val id: String = "",
    @SerialName("product_id") val productId: Int,
    @SerialName("user_id") val userId: String,
    @SerialName("products") val product: ProductEntity? = null,
)