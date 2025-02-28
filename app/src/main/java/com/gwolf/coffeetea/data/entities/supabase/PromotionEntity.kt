package com.gwolf.coffeetea.data.entities.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromotionEntity(
    @SerialName("promotion_id") val id: Int = -1,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    @SerialName("bucket_id") val bucketId: String,
    @SerialName("image_path") val imagePath: String
)