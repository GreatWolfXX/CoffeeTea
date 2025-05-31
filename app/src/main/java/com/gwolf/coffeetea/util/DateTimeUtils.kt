package com.gwolf.coffeetea.util

import timber.log.Timber
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale


object DateTimeUtils {

    fun formatDateTimeFromString(dateTimeString: String, locale: Locale = Locale.getDefault()): String {
        Timber.d("TIME $dateTimeString")
        val inputFormatter = DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .optionalEnd()
            .appendPattern("XXX")
            .toFormatter(locale)

        val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", locale)
        val dateTime = OffsetDateTime.parse(dateTimeString, inputFormatter)
        return dateTime.format(outputFormatter)
    }
}