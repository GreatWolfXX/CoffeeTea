package com.gwolf.coffeetea.data.dto.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("id") val id: String = "",
    @SerialName("category_id") val categoryId: String,
    @SerialName("name") val name: String,
    @SerialName("stock_quantity") val stockQuantity: Int,
    @SerialName("amount") val amount: String,
    @SerialName("unit") val unit: String,
    @SerialName("features_description") val featuresDescription: String,
    @SerialName("full_description") val fullDescription: String,
    @SerialName("price") val price: Double,
    @SerialName("rating") val rating: Double,
    @SerialName("bucket_id") val bucketId: String,
    @SerialName("image_path") val imagePath: String,

    @SerialName("categories") val category: CategoryDto? = null,
    @SerialName("favorites") val favorite: List<FavoriteDto> = emptyList(),
    @SerialName("cart_items") val cartItem: List<CartItemDto> = emptyList()
)