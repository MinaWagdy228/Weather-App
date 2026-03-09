package com.example.wizzar.data.repository

import android.util.Log
import com.example.wizzar.BuildConfig.API_KEY
import com.example.wizzar.data.dataSource.local.dao.CurrentWeatherDao
import com.example.wizzar.data.dataSource.local.dao.ForecastDao
import com.example.wizzar.data.dataSource.remote.api.WeatherService
import com.example.wizzar.data.wrapper.toCurrentWeatherEntity
import com.example.wizzar.data.wrapper.toDomain
import com.example.wizzar.data.wrapper.toEntity
import com.example.wizzar.data.wrapper.toHourlyForecast
import com.example.wizzar.domain.model.CurrentWeather
import com.example.wizzar.domain.model.DailyForecast
import com.example.wizzar.domain.model.HourlyForecast
import com.example.wizzar.domain.model.WeatherData
import com.example.wizzar.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherService: WeatherService,
    private val currentWeatherDao: CurrentWeatherDao,
    private val forecastDao: ForecastDao
) : WeatherRepository {

    override fun observeCurrentWeather(lat: Double, lon: Double): Flow<CurrentWeather?> {
        return currentWeatherDao.observeCurrentWeather(lat, lon).map { it?.toDomain() }
    }

    override fun observeForecast(lat: Double, lon: Double): Flow<List<HourlyForecast>> {
        return forecastDao.observeForecast(lat, lon).map { list -> list.map { it.toHourlyForecast() } }
    }

    override suspend fun getCachedWeather(lat: Double, lon: Double): WeatherData? {
        return try {
            val entity = currentWeatherDao.observeCurrentWeather(lat, lon).first() ?: return null
            val currentWeather = entity.toDomain() ?: return null

            val forecastEntities = forecastDao.observeForecast(lat, lon).first()
            val hourlyForecast = forecastEntities.map { it.toHourlyForecast() }

            val dailyForecast = forecastEntities.groupBy { it.timestamp / 86400 }.map { (_, dayForecasts) ->
                DailyForecast(
                    date = dayForecasts.first().timestamp,
                    minTemp = dayForecasts.minOf { it.temperature },
                    maxTemp = dayForecasts.maxOf { it.temperature },
                    weatherConditionId = dayForecasts.first().weatherId,
                    icon = dayForecasts.first().icon
                )
            }
            WeatherData(currentWeather, hourlyForecast, dailyForecast)
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error getting cached weather", e)
            null
        }
    }

    override suspend fun fetchWeatherFromApi(lat: Double, lon: Double): WeatherData {
        val forecastResponse = weatherService.getForecast(lat = lat, lon = lon, apiKey = API_KEY)

        // Maps using exact GPS coordinates to override API shifts
        val currentWeather = forecastResponse.toCurrentWeatherEntity(lat, lon).toDomain()
            ?: throw Exception("Failed to map fresh network data")

        val forecastEntities = forecastResponse.toEntity(lat, lon)
        val hourlyForecast = forecastEntities.map { it.toHourlyForecast() }

        val dailyForecast = forecastEntities.groupBy { it.timestamp / 86400 }.map { (_, dayForecasts) ->
            DailyForecast(
                date = dayForecasts.first().timestamp,
                minTemp = dayForecasts.minOf { it.temperature },
                maxTemp = dayForecasts.maxOf { it.temperature },
                weatherConditionId = dayForecasts.first().weatherId,
                icon = dayForecasts.first().icon
            )
        }

        return WeatherData(currentWeather, hourlyForecast, dailyForecast)
    }

    override suspend fun saveToCache(weatherData: WeatherData) {
        try {
            val lat = weatherData.currentWeather.latitude
            val lon = weatherData.currentWeather.longitude

            currentWeatherDao.deleteCurrentWeather(lat, lon)
            forecastDao.deleteForecast(lat, lon)

            currentWeatherDao.insertCurrentWeather(weatherData.currentWeather.toEntity())

            val forecastEntities = weatherData.hourlyForecast.map {
                it.toEntity(weatherData.currentWeather.city, lat, lon)
            }
            forecastDao.insertForecast(forecastEntities)

            Log.d("WeatherRepository", "Weather data cached successfully")
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error saving to cache", e)
            throw e
        }
    }
}