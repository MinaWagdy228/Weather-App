package com.example.wizzar.domain.model

data class HourlyForecast(

    val time: Long,

    val temperature: Double,

    val weatherConditionId: Int,
    val icon: String
)