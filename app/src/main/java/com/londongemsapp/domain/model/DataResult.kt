package com.londongemsapp.domain.model

sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Error<T>(val exception: Throwable, val cachedData: T? = null) : DataResult<T>
}
