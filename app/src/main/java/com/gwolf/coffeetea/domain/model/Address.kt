package com.gwolf.coffeetea.domain.model

data class Address(
    val id: String,
    val userId: String,
    val deliveryType: String,
    val refCity: String,
    val refAddress: String,
    val city: String,
    val address: String,
    val isDefault: Boolean
)