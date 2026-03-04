package com.example.wizzar.domain.model

data class CurrentWeather(

    val city: String,

    val temperature: Double,

    val feelsLike: Double,

    val minTemp: Double,

    val maxTemp: Double,

    val humidity: Int,

    val pressure: Int,

    val windSpeed: Double,

    val weatherConditionId: Int,

    val description: String,

    val sunrise: Long,

    val sunset: Long
)