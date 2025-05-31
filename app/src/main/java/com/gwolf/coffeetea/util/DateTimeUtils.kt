package com.gwolf.coffeetea.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


object DateTimeUtils {

    fun formatDateTimeFromString(dateTimeString: String, locale: Locale): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault())
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", locale)
        val dateTime = LocalDateTime.parse(dateTimeString, inputFormatter)
        return dateTime.format(outputFormatter)
    }
}