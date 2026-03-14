package com.example.wizzar.domain.model

data class DailyForecast(
    val date: Long,

    val minTemp: Double,

    val maxTemp: Double,

    val weatherConditionId: Int,
    val icon: String
)