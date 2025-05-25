package com.gwolf.coffeetea.data.dto.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String,
    @SerialName("bucket_id") val bucketId: String,
    @SerialName("image_path") val imagePath: String
)