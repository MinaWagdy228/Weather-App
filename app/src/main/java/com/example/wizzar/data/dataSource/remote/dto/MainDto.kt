package com.example.wizzar.data.dataSource.remote.dto

data class MainDto(
    val temp: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int
)