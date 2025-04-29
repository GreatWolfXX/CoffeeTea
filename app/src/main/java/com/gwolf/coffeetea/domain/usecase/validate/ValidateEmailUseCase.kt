package com.gwolf.coffeetea.domain.usecase.validate

import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.entities.ValidationResult
import com.gwolf.coffeetea.util.UiText
import com.gwolf.coffeetea.util.isEmailValid
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(input: String): ValidationResult {
        if (input.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(resId = R.string.err_field_empty)
            )
        }
        if (!isEmailValid(input)) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(resId = R.string.err_email_valid)
            )
        }
        return ValidationResult(
            successful = true,
            errorMessage = null
        )
    }
}