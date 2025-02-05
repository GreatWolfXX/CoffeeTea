package com.gwolf.coffeetea.data.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileEntity(
    @SerialName("user_id") val id: String = "",
    @SerialName("email") val email: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("patronymic") val patronymic: String,
    @SerialName("phone") val phone: Int?,
    @SerialName("image_path") val imagePath: String
)