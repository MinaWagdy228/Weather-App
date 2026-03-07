package com.example.wizzar.domain.repository

import com.example.wizzar.domain.model.CurrentWeather
import com.example.wizzar.domain.model.HourlyForecast
import com.example.wizzar.domain.model.Location
import com.example.wizzar.domain.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun observeCurrentWeather(): Flow<CurrentWeather?>

    fun observeForecast(): Flow<List<HourlyForecast>>

    suspend fun refreshWeather(location: Location)

    // Methods for RefreshWeatherUseCase
    suspend fun getCachedWeather(): WeatherData?

    suspend fun fetchWeatherFromApi(location: Location): WeatherData

    suspend fun saveToCache(weatherData: WeatherData)

}