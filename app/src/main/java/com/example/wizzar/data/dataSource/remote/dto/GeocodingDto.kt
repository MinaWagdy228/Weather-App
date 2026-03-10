package com.example.wizzar.data.dataSource.remote.dto

import com.google.gson.annotations.SerializedName

data class GeocodingDto(
    @SerializedName("name") val name: String,
    @SerializedName("local_names") val localNames: Map<String, String>?,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("country") val country: String,
)