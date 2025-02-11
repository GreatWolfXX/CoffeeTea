package com.gwolf.coffeetea.domain.usecase.validate

import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.model.ValidationResult
import com.gwolf.coffeetea.util.UiText
import com.gwolf.coffeetea.util.isNameValid
import javax.inject.Inject

class ValidateTextUseCase @Inject constructor() {
    operator fun invoke(input: String): ValidationResult {
        if (!isNameValid(input)) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(resId = R.string.err_field_must_contains_letters)
            )
        }
        return ValidationResult(
            successful = true,
            errorMessage = null
        )
    }
}