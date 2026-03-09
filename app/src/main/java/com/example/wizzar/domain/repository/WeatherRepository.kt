package com.example.wizzar.domain.repository

import com.example.wizzar.domain.model.CurrentWeather
import com.example.wizzar.domain.model.HourlyForecast
import com.example.wizzar.domain.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun observeCurrentWeather(lat: Double, lon: Double): Flow<CurrentWeather?>
    fun observeForecast(lat: Double, lon: Double): Flow<List<HourlyForecast>>
    suspend fun getCachedWeather(lat: Double, lon: Double): WeatherData?
    suspend fun fetchWeatherFromApi(lat: Double, lon: Double): WeatherData
    suspend fun saveToCache(weatherData: WeatherData)
}