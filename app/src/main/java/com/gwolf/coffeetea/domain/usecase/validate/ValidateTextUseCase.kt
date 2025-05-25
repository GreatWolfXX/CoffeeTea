package com.gwolf.coffeetea.domain.usecase.validate

import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.util.ValidationResult
import com.gwolf.coffeetea.util.LocalizedText
import com.gwolf.coffeetea.util.isNameValid
import javax.inject.Inject

class ValidateTextUseCase @Inject constructor() {
    operator fun invoke(input: String): ValidationResult {
        if (!isNameValid(input)) {
            return ValidationResult(
                successful = false,
                errorMessage = LocalizedText.StringResource(resId = R.string.err_field_must_contains_letters)
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}