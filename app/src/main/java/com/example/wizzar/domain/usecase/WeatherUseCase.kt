package com.example.wizzar.domain.usecase

import com.example.wizzar.domain.model.DailyForecast
import com.example.wizzar.domain.model.HourlyForecast
import com.example.wizzar.domain.model.WeatherData
import com.example.wizzar.domain.repository.WeatherRepository
import com.example.wizzar.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class WeatherUseCase @Inject constructor(
    private val repository: WeatherRepository,
    private val refreshWeatherUseCase: RefreshWeatherUseCase
) {

    fun observeWeather(lat : Double, lon : Double): Flow<WeatherData?> {
        return combine(
            repository.observeCurrentWeather(lat, lon),
            repository.observeForecast(lat, lon)
        ) { current, forecast ->

            if (current == null || forecast.isEmpty()) {
                return@combine null
            }

            val hourly = forecast.map {
                HourlyForecast(
                    time = it.time,
                    temperature = it.temperature,
                    weatherConditionId = it.weatherConditionId,
                    icon = it.icon,
                    longitude = it.longitude,
                    latitude = it.latitude
                )
            }

            val daily = forecast
                .groupBy { it.time / 86400 }
                .map { (_, day) ->
                    DailyForecast(
                        date = day.first().time,
                        minTemp = day.minOf { it.temperature },
                        maxTemp = day.maxOf { it.temperature },
                        weatherConditionId = day.first().weatherConditionId,
                        icon = day.first().icon
                    )
                }

            WeatherData(
                currentWeather = current,
                hourlyForecast = hourly,
                dailyForecast = daily
            )
        }
    }

    suspend fun refreshWeather(latitude : Double, longitude : Double, forceRefresh: Boolean = false): Result<WeatherData> {
        return refreshWeatherUseCase.execute(latitude, longitude, forceRefresh)
    }
}