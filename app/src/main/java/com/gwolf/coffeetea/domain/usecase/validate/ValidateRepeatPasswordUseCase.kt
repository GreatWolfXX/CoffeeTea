package com.gwolf.coffeetea.domain.usecase.validate

import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.util.ValidationResult
import com.gwolf.coffeetea.util.LocalizedText
import com.gwolf.coffeetea.util.isRepeatPasswordValid
import javax.inject.Inject

class ValidateRepeatPasswordUseCase @Inject constructor() {
    operator fun invoke(password: String, passwordRepeat: String): ValidationResult {

        if (!isRepeatPasswordValid(password, passwordRepeat)) {
            return ValidationResult(
                successful = false,
                errorMessage = LocalizedText.StringResource(resId = R.string.err_repeat_password_valid),
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}