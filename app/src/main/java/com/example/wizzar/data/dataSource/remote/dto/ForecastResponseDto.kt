package com.example.wizzar.data.dataSource.remote.dto

data class ForecastResponseDto(

    val list: List<ForecastItemDto>,
    val city: CityDto

)

data class ForecastItemDto(

    val dt: Long,

    val main: MainDto,

    val weather: List<WeatherDto>

)

data class CityDto(
    val name: String
)