package com.gwolf.coffeetea.domain.model

import com.gwolf.coffeetea.util.UiText

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: UiText? = null
)