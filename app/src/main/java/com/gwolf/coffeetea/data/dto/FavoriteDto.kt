package com.gwolf.coffeetea.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    @SerialName("favorite_id") val id: Int,
    @SerialName("products") val product: ProductDto,
)