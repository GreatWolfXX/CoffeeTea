package com.gwolf.coffeetea.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("product_id") val id: Int,
    @SerialName("product_name") val name: String,
    @SerialName("amount") val amount: Double,
    @SerialName("unit") val unit: String,
    @SerialName("features_description") val featuresDescription: String?,
    @SerialName("full_description") val fullDescription: String?,
    @SerialName("price") val price: Double,
    @SerialName("rating") val rating: Double?,
    @SerialName("bucket_id") val bucketId: String,
    @SerialName("category") val category: CategoryDto? = null,
    @SerialName("image_path") val imagePath: String,
    @SerialName("favorite") val favorite: List<FavoriteDto> = emptyList<FavoriteDto>(),
)