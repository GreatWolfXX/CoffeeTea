package com.gwolf.coffeetea.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class LocalizedText {
    data class DynamicString(val value: String) : LocalizedText()
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : LocalizedText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}