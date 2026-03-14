package com.example.wizzar.data.dataSource.local

import com.example.wizzar.data.dataSource.local.dao.CurrentWeatherDao
import com.example.wizzar.data.dataSource.local.dao.ForecastDao
import com.example.wizzar.data.dataSource.local.entity.CurrentWeatherEntity
import com.example.wizzar.data.dataSource.local.entity.ForecastEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherLocalDataSourceImpl @Inject constructor(
    private val currentWeatherDao: CurrentWeatherDao,
    private val forecastDao: ForecastDao
) : WeatherLocalDataSource {

    override fun observeCurrentWeather(lat: Double, lon: Double): Flow<CurrentWeatherEntity?> {
        return currentWeatherDao.observeCurrentWeather(lat, lon)
    }

    override fun observeForecast(lat: Double, lon: Double): Flow<List<ForecastEntity>> {
        return forecastDao.observeForecast(lat, lon)
    }

    override suspend fun insertCurrentWeather(currentWeatherEntity: CurrentWeatherEntity) {
        currentWeatherDao.insertCurrentWeather(currentWeatherEntity)
    }

    override suspend fun deleteCurrentWeather(latitude: Double, longitude: Double) {
        currentWeatherDao.deleteCurrentWeather(latitude, longitude)
    }

    override suspend fun insertForecast(forecast: List<ForecastEntity>) {
        forecastDao.insertForecast(forecast)
    }

    override suspend fun deleteForecast(latitude: Double, longitude: Double) {
        forecastDao.deleteForecast(latitude, longitude)
    }
}