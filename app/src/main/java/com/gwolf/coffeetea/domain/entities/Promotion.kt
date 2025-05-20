package com.gwolf.coffeetea.domain.entities

data class Promotion(
    val id: String,
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val imageUrl: String
)