package com.example.core.ui.utils



sealed class UIState<out T> {
    data class Success<T>(val data: T) : UIState<T>()
    data class Error(val error: String) : UIState<Nothing>()
    data object Loading : UIState<Nothing>()
    data object Empty : UIState<Nothing>()
}
