package com.gwolf.coffeetea.domain.usecase.validate

import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.util.ValidationResult
import com.gwolf.coffeetea.util.LocalizedText
import com.gwolf.coffeetea.util.isPhoneNumber
import javax.inject.Inject

class ValidatePhoneUseCase @Inject constructor() {
    operator fun invoke(phone: String, oldPhone: String = ""): ValidationResult {
        if (phone.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = LocalizedText.StringResource(resId = R.string.err_field_empty)
            )
        }
        if (!isPhoneNumber(phone)) {
            return ValidationResult(
                successful = false,
                errorMessage = LocalizedText.StringResource(resId = R.string.err_phone_valid)
            )
        }
        if (oldPhone.isNotEmpty() && phone == oldPhone) {
            return ValidationResult(
                successful = false,
                errorMessage = LocalizedText.StringResource(resId = R.string.err_phone_same)
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}