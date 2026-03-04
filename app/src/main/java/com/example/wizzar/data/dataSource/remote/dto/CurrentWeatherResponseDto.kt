package com.example.wizzar.data.dataSource.remote.dto

data class CurrentWeatherResponseDto(
    val name: String,

    val dt: Long,

    val weather: List<WeatherDto>,

    val main: MainDto,

    val wind : WindDto,

    val sys: SysDto

) {
    data class SysDto(
        val sunrise: Long,
        val sunset: Long
    )
    data class WindDto(
        val speed: Double
    )
}