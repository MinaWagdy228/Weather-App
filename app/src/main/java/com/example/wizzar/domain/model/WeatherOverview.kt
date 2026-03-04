package com.example.wizzar.domain.model

data class WeatherOverview(
    val currentWeather : CurrentWeather,
    val hourlyWeather : List<HourlyForecast>,
    val dailyWeather : List<DailyWeather>
)