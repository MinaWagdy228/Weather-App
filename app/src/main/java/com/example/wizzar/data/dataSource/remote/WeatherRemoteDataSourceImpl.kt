package com.example.wizzar.data.dataSource.remote

import com.example.wizzar.data.dataSource.remote.api.WeatherService
import com.example.wizzar.data.dataSource.remote.dto.ForecastResponseDto
import com.example.wizzar.data.dataSource.remote.dto.GeocodingDto
import javax.inject.Inject

class WeatherRemoteDataSourceImpl @Inject constructor(
    private val weatherService: WeatherService
) : WeatherRemoteDataSource {

    override suspend fun getForecast(lat: Double, lon: Double, apiKey: String, lang: String): ForecastResponseDto {
        return weatherService.getForecast(lat = lat, lon = lon, apiKey = apiKey, lang = lang)
    }

    override suspend fun reverseGeocode(lat: Double, lon: Double, apiKey: String): List<GeocodingDto> {
        return weatherService.reverseGeocode(lat = lat, lon = lon, apiKey = apiKey)
    }

    override suspend fun searchCityByName(query: String, apiKey: String): List<GeocodingDto> {
        return weatherService.searchCityByName(query = query, apiKey = apiKey)
    }
}