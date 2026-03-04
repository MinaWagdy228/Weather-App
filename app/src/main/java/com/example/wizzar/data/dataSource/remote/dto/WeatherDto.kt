package com.example.wizzar.data.dataSource.remote.dto

data class WeatherDto(
    val id: Int,
    val icon: String,
    val main: String,
    val description: String
)