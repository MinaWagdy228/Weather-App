package com.example.wizzar.data.dataSource.remote.api

import com.example.wizzar.data.dataSource.remote.dto.ForecastResponseDto
import com.example.wizzar.data.dataSource.remote.dto.GeocodingDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String
    ): ForecastResponseDto

    @GET("geo/1.0/reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): List<GeocodingDto>

    @GET("geo/1.0/direct")
    suspend fun searchCityByName(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): List<GeocodingDto>
}