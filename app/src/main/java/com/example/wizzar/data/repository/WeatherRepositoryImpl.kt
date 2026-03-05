package com.example.wizzar.data.repository

import com.example.wizzar.BuildConfig.API_KEY
import com.example.wizzar.data.dataSource.local.dao.CurrentWeatherDao
import com.example.wizzar.data.dataSource.local.dao.ForecastDao
import com.example.wizzar.data.dataSource.remote.api.WeatherService
import com.example.wizzar.data.wrapper.toDomain
import com.example.wizzar.data.wrapper.toEntity
import com.example.wizzar.data.wrapper.toHourlyForecast
import com.example.wizzar.domain.model.CurrentWeather
import com.example.wizzar.domain.model.HourlyForecast
import com.example.wizzar.domain.model.Location
import com.example.wizzar.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(

    private val weatherService: WeatherService,
    private val currentWeatherDao: CurrentWeatherDao,
    private val forecastDao: ForecastDao

) : WeatherRepository {
    var cityName: String = "";

    override fun observeCurrentWeather(): Flow<CurrentWeather?> {

        return currentWeatherDao
            .observeCurrentWeather()
            .map { entity ->
                cityName = entity?.cityName ?: ""
                entity?.toDomain()
            }
    }

    override fun observeForecast(): Flow<List<HourlyForecast>> {
        return forecastDao.observeForecast(
            cityName = cityName
        ).map {
            it.map { entity ->

                entity.toHourlyForecast()

            }
        }
    }

    override suspend fun refreshWeather(location: Location) {

        val currentResponse =
            weatherService.getCurrentWeather(
                location.latitude,
                location.longitude,
                API_KEY
            )

        val forecastResponse =
            weatherService.getForecast(
                location.latitude,
                location.longitude,
                API_KEY
            )

        currentWeatherDao.insertCurrentWeather(
            currentResponse.toEntity()
        )

        forecastDao.insertForecast(
            forecastResponse.toEntity()
        )

    }

}