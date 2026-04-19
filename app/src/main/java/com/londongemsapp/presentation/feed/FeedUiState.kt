package com.londongemsapp.presentation.feed

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String, val cachedData: Any? = null) : UiState<Nothing>
}
