package com.gwolf.coffeetea.domain.entities

data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val body: String,
    val timestamp: String,
    val type: String,
    val isRead: Boolean
)