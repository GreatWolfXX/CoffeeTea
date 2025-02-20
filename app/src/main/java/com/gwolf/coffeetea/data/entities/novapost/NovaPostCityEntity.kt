package com.gwolf.coffeetea.data.entities.novapost

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovaPostCityEntity (
    @SerialName("Ref") val ref: String,
    @SerialName("Description") val name: String
)