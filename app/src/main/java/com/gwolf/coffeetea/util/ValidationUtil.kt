package com.gwolf.coffeetea.util

import android.util.Patterns

fun isPhoneNumber(value: String): Boolean {
    return value.isEmpty() || Regex("^\\+\\d+\$").matches(value)
}

fun isEmailValid(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isPasswordValid(password: String): Boolean {
    return password.any { it.isDigit() } &&
            password.any { it.isLetter() }
}

fun isRepeatPasswordValid(password: String, repeatPassword: String): Boolean {
    return password == repeatPassword
}