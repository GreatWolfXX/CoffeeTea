package com.gwolf.coffeetea.util

sealed class UiResult<out R> {
    data class Success<out T>(val data: T) : UiResult<T>()
    data class Error(val exception: Exception) : UiResult<Nothing>()
}