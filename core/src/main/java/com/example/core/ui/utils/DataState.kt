package com.example.core.ui.utils

sealed class DataState<out R> {
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val throwable: Throwable) : DataState<Nothing>()
    data object Loading : DataState<Nothing>()
    data object Idle : DataState<Nothing>()
}