package com.gwolf.coffeetea.util

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: LocalizedText = LocalizedText.DynamicString("")
)