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

    fun observeWeather(): Flow<WeatherData?> {
        return combine(
            repository.observeCurrentWeather(),
            repository.observeForecast()
        ) { current, forecast ->

            if (current == null || forecast.isEmpty()) {
                return@combine null
            }

            val hourly = forecast.map {
                HourlyForecast(
                    time = it.time,
                    temperature = it.temperature,
                    weatherConditionId = it.weatherConditionId,
                    icon = it.icon
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

    suspend fun refreshWeather(forceRefresh: Boolean = false): Result<WeatherData> {
        return refreshWeatherUseCase.execute(forceRefresh) // Pass it down
    }
}