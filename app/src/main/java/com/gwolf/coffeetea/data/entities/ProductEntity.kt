package com.gwolf.coffeetea.data.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductEntity(
    @SerialName("product_id") val id: Int = -1,
    @SerialName("product_name") val name: String,
    @SerialName("stock_quantity") val stockQuantity: Int,
    @SerialName("amount") val amount: Double,
    @SerialName("unit") val unit: String,
    @SerialName("features_description") val featuresDescription: String,
    @SerialName("full_description") val fullDescription: String,
    @SerialName("price") val price: Double,
    @SerialName("rating") val rating: Double,
    @SerialName("bucket_id") val bucketId: String,
    @SerialName("image_path") val imagePath: String,
    @SerialName("category_id") val categoryId: Int,
    @SerialName("categories") val category: CategoryEntity? = null,
    @SerialName("favorites") val favorite: List<FavoriteEntity> = emptyList<FavoriteEntity>(),
    @SerialName("cart") val cart: List<CartEntity> = emptyList<CartEntity>()
)