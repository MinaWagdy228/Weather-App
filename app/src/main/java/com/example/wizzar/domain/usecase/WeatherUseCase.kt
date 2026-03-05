package com.example.wizzar.domain.usecase

import com.example.wizzar.domain.model.HourlyForecast
import com.example.wizzar.domain.model.DailyForecast
import com.example.wizzar.domain.model.Location
import com.example.wizzar.domain.model.WeatherOverview
import com.example.wizzar.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class WeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {

    fun observeWeather(): Flow<WeatherOverview> {

        return combine(
            repository.observeCurrentWeather(),
            repository.observeForecast()
        ) { current, forecast ->

            val hourly = forecast.map {

                HourlyForecast(
                    time = it.time,
                    temperature = it.temperature,
                    weatherConditionId = it.weatherConditionId
                )

            }

            val daily = forecast
                .groupBy { it.time / 86400 }
                .map { (_, day) ->

                    DailyForecast(
                        date = day.first().time,
                        minTemp = day.minOf { it.temperature },
                        maxTemp = day.maxOf { it.temperature },
                        weatherConditionId = day.first().weatherConditionId
                    )

                }

            WeatherOverview(
                currentWeather = current,
                hourlyWeather = hourly,
                dailyWeather = daily
            )

        }

    }

    suspend fun refreshWeather(location: Location) {
        repository.refreshWeather(location)
    }

}