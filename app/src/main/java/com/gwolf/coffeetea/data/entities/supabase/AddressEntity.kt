package com.gwolf.coffeetea.data.entities.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressEntity(
    @SerialName("address_id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("delivery_type") val deliveryType: String,
    @SerialName("ref_city") val refCity: String,
    @SerialName("ref_address") val refAddress: String,
    @SerialName("city") val city: String,
    @SerialName("address") val address: String,
    @SerialName("is_default") val isDefault: Boolean
)