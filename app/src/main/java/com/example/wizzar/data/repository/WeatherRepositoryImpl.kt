package com.example.wizzar.data.repository

import android.util.Log
import com.example.wizzar.BuildConfig.API_KEY
import com.example.wizzar.data.dataSource.local.WeatherLocalDataSource
import com.example.wizzar.data.dataSource.remote.WeatherRemoteDataSource
import com.example.wizzar.data.mapper.toCurrentWeatherEntity
import com.example.wizzar.data.mapper.toDomain
import com.example.wizzar.data.mapper.toEntity
import com.example.wizzar.domain.model.CurrentWeather
import com.example.wizzar.domain.model.DailyForecast
import com.example.wizzar.domain.model.HourlyForecast
import com.example.wizzar.domain.model.LocationSearchResult
import com.example.wizzar.domain.model.WeatherData
import com.example.wizzar.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
) : WeatherRepository {

    override fun observeCurrentWeather(lat: Double, lon: Double): Flow<CurrentWeather?> {
        return localDataSource.observeCurrentWeather(lat, lon).map { it?.toDomain() }
    }

    override fun observeForecast(lat: Double, lon: Double): Flow<List<HourlyForecast>> {
        return localDataSource.observeForecast(lat, lon)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getCachedWeather(lat: Double, lon: Double): WeatherData? {
        return try {
            val entity = localDataSource.observeCurrentWeather(lat, lon).first() ?: return null
            val currentWeather = entity.toDomain() ?: return null

            val forecastEntities = localDataSource.observeForecast(lat, lon).first()
            val hourlyForecast = forecastEntities.map { it.toDomain() }

            val dailyForecast =
                forecastEntities.groupBy { it.timestamp / 86400 }.map { (_, dayForecasts) ->
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

    override suspend fun fetchWeatherFromApi(lat: Double, lon: Double, lang: String): WeatherData {
        val forecastResponse =
            remoteDataSource.getForecast(lat = lat, lon = lon, apiKey = API_KEY, lang = lang)

        // Maps using exact GPS coordinates to override API shifts
        val currentWeather = forecastResponse.toCurrentWeatherEntity(lat, lon).toDomain()
            ?: throw Exception("Failed to map fresh network data")

        val forecastEntities = forecastResponse.toEntity(lat, lon)
        val hourlyForecast = forecastEntities.map { it.toDomain() }

        val dailyForecast =
            forecastEntities.groupBy { it.timestamp / 86400 }.map { (_, dayForecasts) ->
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

    override suspend fun searchLocations(query: String): List<LocationSearchResult> {
        return try {
            val dtoList = remoteDataSource.searchCityByName(query = query, apiKey = API_KEY)
            dtoList.map {
                it.toDomain()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getCityNameFromCoordinates(
        lat: Double,
        lon: Double,
        lang: String
    ): String? {
        return try {
            val dtoList = remoteDataSource.reverseGeocode(lat = lat, lon = lon, apiKey = API_KEY)
            val location = dtoList.firstOrNull()
            if (lang == "ar") {
                location?.localNames?.get("ar") ?: location?.name
            } else {
                location?.name
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveToCache(weatherData: WeatherData) {
        try {
            val lat = weatherData.currentWeather.latitude
            val lon = weatherData.currentWeather.longitude

            localDataSource.deleteCurrentWeather(lat, lon)
            localDataSource.deleteForecast(lat, lon)

            localDataSource.insertCurrentWeather(weatherData.currentWeather.toEntity())

            val forecastEntities = weatherData.hourlyForecast.map {
                it.toEntity(weatherData.currentWeather.city, lat, lon)
            }
            localDataSource.insertForecast(forecastEntities)

            Log.d("WeatherRepository", "Weather data cached successfully")
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error saving to cache", e)
            throw e
        }
    }
}