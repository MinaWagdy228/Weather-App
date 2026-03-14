package com.example.wizzar.data.dataSource.remote

import com.example.wizzar.data.dataSource.remote.dto.ForecastResponseDto
import com.example.wizzar.data.dataSource.remote.dto.GeocodingDto

interface WeatherRemoteDataSource {
    suspend fun getForecast(lat: Double, lon: Double, apiKey: String, lang: String): ForecastResponseDto
    suspend fun reverseGeocode(lat: Double, lon: Double, apiKey: String): List<GeocodingDto>
    suspend fun searchCityByName(query: String, apiKey: String): List<GeocodingDto>
}