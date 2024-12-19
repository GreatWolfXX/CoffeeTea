package com.gwolf.coffeetea.domain.model

import java.util.UUID

data class Profile(
    val id: UUID,
    val email: String,
    val name: String?,
    val bucketId: String,
    val imageUrl: String
)