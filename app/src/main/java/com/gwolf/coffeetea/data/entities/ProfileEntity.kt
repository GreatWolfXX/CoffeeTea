package com.gwolf.coffeetea.data.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileEntity(
    @SerialName("user_id") val id: String = "",
    @SerialName("email") val email: String,
    @SerialName("display_name") val name: String,
    @SerialName("bucket_id") val bucketId: String,
    @SerialName("image_path") val imagePath: String
)