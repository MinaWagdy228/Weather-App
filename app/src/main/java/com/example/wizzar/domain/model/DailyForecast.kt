package com.example.wizzar.domain.model

data class DailyWeather(
    val date: Long,

    val minTemp: Double,

    val maxTemp: Double,

    val humidity: Int,

    val weatherConditionId: Int
)