package com.gwolf.coffeetea.data.dto.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String?,
    @SerialName("title") val title: String,
    @SerialName("body") val body: String,
    @SerialName("timestamp") val timestamp: String,
    @SerialName("type") val type: String,
    @SerialName("is_read") val isRead: Boolean
)