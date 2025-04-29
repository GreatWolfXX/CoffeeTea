package com.gwolf.coffeetea.data.dto.novapost

import kotlinx.serialization.Serializable

@Serializable
data class NovaPostResponse<T>(
    val success: Boolean,
    val data: List<T>
)