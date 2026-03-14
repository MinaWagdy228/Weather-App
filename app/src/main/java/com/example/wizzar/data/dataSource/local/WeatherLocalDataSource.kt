package com.example.wizzar.data.dataSource.local

import com.example.wizzar.data.dataSource.local.entity.CurrentWeatherEntity
import com.example.wizzar.data.dataSource.local.entity.ForecastEntity
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    fun observeCurrentWeather(lat: Double, lon: Double): Flow<CurrentWeatherEntity?>
    fun observeForecast(lat: Double, lon: Double): Flow<List<ForecastEntity>>
    suspend fun insertCurrentWeather(currentWeatherEntity: CurrentWeatherEntity)
    suspend fun deleteCurrentWeather(latitude: Double, longitude: Double)
    suspend fun insertForecast(forecast: List<ForecastEntity>)
    suspend fun deleteForecast(latitude: Double, longitude: Double)
}