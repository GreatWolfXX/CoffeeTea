package com.gwolf.coffeetea.domain.entities

import java.util.UUID

data class Profile(
    val id: UUID,
    val email: String,
    val firstName: String,
    val lastName: String,
    val patronymic: String,
    val phone: String,
    val imageUrl: String
)