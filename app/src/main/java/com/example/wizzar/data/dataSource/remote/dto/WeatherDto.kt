package com.example.wizzar.data.dataSource.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("icon")
    val icon: String,

    @SerializedName("main")
    val main: String,

    @SerializedName("description")
    val description: String
)