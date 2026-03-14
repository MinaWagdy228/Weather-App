package com.example.wizzar.data.dataSource.remote.dto

import com.google.gson.annotations.SerializedName

data class ForecastResponseDto(
    @SerializedName("list") val list: List<ForecastItemDto>,
    @SerializedName("city") val city: CityDto
)

data class ForecastItemDto(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: MainDto,
    @SerializedName("weather") val weather: List<WeatherDto>,
    @SerializedName("wind") val wind: WindDto
)

data class WindDto(
    @SerializedName("speed") val speed: Double
)

data class CityDto(
    @SerializedName("name") val name: String,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long,
    @SerializedName("coord") val coord: CoordDto
)

data class CoordDto(
    @SerializedName("lon") val lon: Double,
    @SerializedName("lat") val lat: Double
)