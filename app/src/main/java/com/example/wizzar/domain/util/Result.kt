package com.example.wizzar.domain.util

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: DomainError) : Result<Nothing>()
}

sealed class DomainError(val message: String) {
    data class LocationServiceDisabledError(val msg: String = "Location service is disabled") : DomainError(msg)
    data class IncompleteDataError(val msg: String = "Weather data missing required fields") : DomainError(msg)
    data class NoDataAvailableError(val msg: String = "No internet and no cached data") : DomainError(msg)
    data class NetworkError(val msg: String = "Network error occurred") : DomainError(msg)
}

