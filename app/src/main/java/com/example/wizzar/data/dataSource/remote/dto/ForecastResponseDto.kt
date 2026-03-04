package com.example.wizzar.data.dataSource.remote.dto

data class ForecastResponseDto(

    val list: List<ForecastItemDto>,
    val city: String

)

data class ForecastItemDto(

    val dt: Long,

    val main: MainDto,

    val weather: List<WeatherDto>

)