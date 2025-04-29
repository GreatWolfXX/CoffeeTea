package com.gwolf.coffeetea.data.dto.novapost

import kotlinx.serialization.Serializable

@Serializable
data class NovaPostBody(
    val apiKey: String,
    val modelName: String,
    val calledMethod: String,
    val methodProperties: NovaPostProperties
)