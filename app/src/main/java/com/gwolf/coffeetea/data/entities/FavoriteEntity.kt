package com.gwolf.coffeetea.data.entities

import androidx.room.Entity
import com.gwolf.coffeetea.util.FAVORITES_TABLE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = FAVORITES_TABLE)
data class FavoriteEntity(
    @SerialName("favorite_id") val id: Int = -1,
    @SerialName("product_id") val productId: Int,
    @SerialName("user_id") val userId: String,
    @SerialName("products") val product: ProductEntity? = null,
)