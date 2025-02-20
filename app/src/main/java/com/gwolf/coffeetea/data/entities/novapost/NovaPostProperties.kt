package com.gwolf.coffeetea.data.entities.novapost

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class NovaPostProperties {

    @Serializable
    data class GetCity(
        @SerialName("FindByString") val findByString: String
    ): NovaPostProperties()

    @Serializable
    data class GetDepartments(
        @SerialName("CityRef") val cityRef: String,
        @SerialName("TypeOfWarehouseRef") val typeOfWarehouseRef: String,
        @SerialName("FindByString") val findByString: String
    ): NovaPostProperties()

}