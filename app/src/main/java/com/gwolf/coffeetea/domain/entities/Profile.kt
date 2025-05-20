package com.gwolf.coffeetea.domain.entities

data class Profile(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val patronymic: String,
    val phone: String,
    val imageUrl: String
)