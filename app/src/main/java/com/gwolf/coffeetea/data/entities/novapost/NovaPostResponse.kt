package com.gwolf.coffeetea.data.entities.novapost

import kotlinx.serialization.Serializable

@Serializable
data class NovaPostResponse<T>(
    val success: Boolean,
    val data: List<T>
)