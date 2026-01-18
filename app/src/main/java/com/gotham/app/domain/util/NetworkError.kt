package com.gotham.app.domain.util

sealed class NetworkError {
    data object NoInternet : NetworkError()
    data object RateLimit : NetworkError()
    data object Timeout : NetworkError()
    data object ServerError : NetworkError()
    data object NotFound : NetworkError()
    data object Unauthorized : NetworkError()
    data class Unknown(val code: Int? = null) : NetworkError()

    fun toUserMessage(): String = when (this) {
        is NoInternet -> "No internet connection. Please check your network."
        is RateLimit -> "Too many requests. Please try again later."
        is Timeout -> "Request timed out. Please try again."
        is ServerError -> "Server error. Please try again later."
        is NotFound -> "Resource not found."
        is Unauthorized -> "Unauthorized. Please check your API token."
        is Unknown -> "An unexpected error occurred${code?.let { " (code: $it)" } ?: ""}."
    }
}
