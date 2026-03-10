package com.example.wizzar.domain.model

data class LocationSearchResult(
    val name: String,
    val country: String,
    val state: String?,
    val latitude: Double,
    val longitude: Double,
    val localizedName: String?
)