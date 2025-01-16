package com.gwolf.coffeetea.data.entities

import androidx.room.Entity
import com.gwolf.coffeetea.util.CATEGORIES_TABLE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = CATEGORIES_TABLE)
data class CategoryEntity(
    @SerialName("category_id") val id: Int = -1,
    @SerialName("category_name") val name: String,
    @SerialName("bucket_id") val bucketId: String,
    @SerialName("image_path") val imagePath: String
)