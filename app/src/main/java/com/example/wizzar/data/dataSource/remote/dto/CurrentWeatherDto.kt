package com.example.wizzar.data.dataSource.remote.dto

data class CurrentWeatherDto(

    val dt: Long,

    val weather: List<WeatherDto>,

    val main: MainDto,

    val windSpeed: Double,

    val sys: SysDto

) {
    data class SysDto(
        val sunrise: Long,
        val sunset: Long
    )
}