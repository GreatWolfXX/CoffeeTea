package com.gwolf.coffeetea.domain.model

data class Promotion(
    val id: Int,
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val imageUrl: String
)