package com.gwolf.coffeetea.domain.usecase.validate

import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.util.ValidationResult
import com.gwolf.coffeetea.util.MIN_PASSWORD_LENGTH
import com.gwolf.coffeetea.util.LocalizedText
import com.gwolf.coffeetea.util.isPasswordValid
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {
    operator fun invoke(input: String): ValidationResult {
        if (input.length < MIN_PASSWORD_LENGTH) {
            return ValidationResult(
                successful = false,
                errorMessage = LocalizedText.StringResource(resId = R.string.err_password_length, MIN_PASSWORD_LENGTH),
            )
        }

        if (!isPasswordValid(input)) {
            return ValidationResult(
                successful = false,
                errorMessage = LocalizedText.StringResource(resId = R.string.err_password_valid),
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}