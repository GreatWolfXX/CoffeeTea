package com.gwolf.coffeetea.domain.usecase.validate

import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.model.ValidationResult
import com.gwolf.coffeetea.util.UiText
import javax.inject.Inject

class ValidateTextUseCase @Inject constructor() {
    operator fun invoke(input: String): ValidationResult {
        if (input.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(resId = R.string.err_field_empty)
            )
        }
        return ValidationResult(
            successful = true,
            errorMessage = null
        )
    }
}