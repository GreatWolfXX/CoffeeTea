package com.gwolf.coffeetea.domain.model

import java.util.UUID

data class Profile(
    val id: UUID,
    val email: String,
    val firstName: String,
    val lastName: String,
    val patronymic: String,
    val phone: Int?,
    val bucketId: String,
    val imageUrl: String
)