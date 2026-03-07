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
import com.example.wizzar.domain.model.Location
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

    override fun observeCurrentWeather(): Flow<CurrentWeather?> {
        return currentWeatherDao
            .observeCurrentWeather()
            .map { entity ->
                // Use the safe call operator '?.'. If the DB is empty, this passes 'null' to the Domain layer.
                entity?.toDomain()
            }
    }

    override fun observeForecast(): Flow<List<HourlyForecast>> {
        return forecastDao.observeForecast().map { list ->
            list.map { entity ->
                entity.toHourlyForecast()
            }
        }
    }

    override suspend fun refreshWeather(location: Location) {
        val weatherData = fetchWeatherFromApi(location)

        // Delete old data before saving new (single location design)
        currentWeatherDao.deleteAllCurrentWeather()
        forecastDao.deleteAllForecast()

        saveToCache(weatherData)
    }

    override suspend fun getCachedWeather(): WeatherData? {
        return try {
            // Null safety check: If the current weather table is empty, we don't have a valid cache
            val entity = currentWeatherDao.observeCurrentWeather().first()
            if (entity == null) return null

            val currentWeather = entity.toDomain()

            val forecastEntities = forecastDao.observeForecast().first()
            val hourlyForecast = forecastEntities.map { it.toHourlyForecast() }

            // Group forecast by day to create daily forecast
            val dailyForecast = forecastEntities
                .groupBy {
                    // Get the day number from timestamp (epoch seconds / 86400)
                    it.timestamp / 86400
                }
                .map { (_, dayForecasts) ->
                    DailyForecast(
                        date = dayForecasts.first().timestamp,
                        minTemp = dayForecasts.minOf { it.temperature },
                        maxTemp = dayForecasts.maxOf { it.temperature },
                        weatherConditionId = dayForecasts.first().weatherId,
                        icon = dayForecasts.first().icon
                    )
                }

            WeatherData(
                currentWeather = currentWeather,
                hourlyForecast = hourlyForecast,
                dailyForecast = dailyForecast
            )

        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error getting cached weather", e)
            null
        }
    }

    override suspend fun fetchWeatherFromApi(location: Location): WeatherData {
        // 1. Single API call using the forecast endpoint
        val forecastResponse = weatherService.getForecast(
            lat = location.latitude,
            lon = location.longitude,
            apiKey = API_KEY
        )

        // 2. Map the very first item to CurrentWeather
        val currentWeather = forecastResponse.toCurrentWeatherEntity().toDomain()

        // 3. Map the rest of the list for hourly/daily
        val forecastEntities = forecastResponse.toEntity()
        val hourlyForecast = forecastEntities.map { it.toHourlyForecast() }

        // 4. Group by day for the 5-day forecast
        val dailyForecast = forecastEntities
            .groupBy { it.timestamp / 86400 } // Group by Day (Epoch / 86400 seconds)
            .map { (_, dayForecasts) ->
                DailyForecast(
                    date = dayForecasts.first().timestamp,
                    minTemp = dayForecasts.minOf { it.temperature },
                    maxTemp = dayForecasts.maxOf { it.temperature },
                    weatherConditionId = dayForecasts.first().weatherId,
                    icon = dayForecasts.first().icon
                )
            }

        return WeatherData(
            currentWeather = currentWeather,
            hourlyForecast = hourlyForecast,
            dailyForecast = dailyForecast
        )
    }

    override suspend fun saveToCache(weatherData: WeatherData) {
        try {
            // Save current weather to database
            currentWeatherDao.insertCurrentWeather(weatherData.currentWeather.toEntity())

            // Save forecast to database
            val forecastEntities = weatherData.hourlyForecast.map {
                it.toEntity(weatherData.currentWeather.city)
            }
            forecastDao.insertForecast(forecastEntities)

            Log.d("WeatherRepository", "Weather data cached successfully")
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error saving to cache", e)
            throw e
        }
    }
}