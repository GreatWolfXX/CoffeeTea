package com.gwolf.coffeetea.data.dto.novapost

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovaPostCityDto (
    @SerialName("Ref") val ref: String,
    @SerialName("Description") val name: String
)