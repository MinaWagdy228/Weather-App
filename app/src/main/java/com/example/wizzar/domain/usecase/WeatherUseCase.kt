package com.example.wizzar.domain.usecase

import com.example.wizzar.domain.model.CurrentWeather
import com.example.wizzar.domain.model.DailyForecast
import com.example.wizzar.domain.model.HourlyForecast
import com.example.wizzar.domain.model.WeatherData
import com.example.wizzar.domain.repository.SettingsRepository
import com.example.wizzar.domain.repository.WeatherRepository
import com.example.wizzar.domain.model.Result
import com.example.wizzar.domain.util.UnitConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class WeatherUseCase @Inject constructor(
    private val repository: WeatherRepository,
    private val settingsRepository: SettingsRepository,
    private val refreshWeatherUseCase: RefreshWeatherUseCase
) {

    fun observeWeather(lat: Double, lon: Double): Flow<WeatherData?> {
        return combine(
            repository.observeCurrentWeather(lat, lon),
            repository.observeForecast(lat, lon),
            settingsRepository.observeSettings()
        ) { current, forecast, settings ->

            if (current == null || forecast.isEmpty()) {
                return@combine null
            }

            val convertedCurrent = current.copy(
                temperature = UnitConverter.convertTemperature(
                    current.temperature,
                    settings.tempUnit
                ),
                feelsLike = UnitConverter.convertTemperature(current.feelsLike, settings.tempUnit),
                minTemp = UnitConverter.convertTemperature(current.minTemp, settings.tempUnit),
                maxTemp = UnitConverter.convertTemperature(current.maxTemp, settings.tempUnit),
                wind = UnitConverter.convertWindSpeed(current.wind, settings.windUnit)
            )

            val hourly = forecast.map {
                HourlyForecast(
                    time = it.time,
                    temperature = UnitConverter.convertTemperature(
                        it.temperature,
                        settings.tempUnit
                    ),
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
                        minTemp = UnitConverter.convertTemperature(
                            day.minOf { it.temperature },
                            settings.tempUnit
                        ),
                        maxTemp = UnitConverter.convertTemperature(
                            day.maxOf { it.temperature },
                            settings.tempUnit
                        ),
                        weatherConditionId = day.first().weatherConditionId,
                        icon = day.first().icon
                    )
                }

            WeatherData(
                currentWeather = convertedCurrent,
                hourlyForecast = hourly,
                dailyForecast = daily
            )
        }
    }

    suspend fun refreshWeather(
        latitude: Double,
        longitude: Double,
        lang: String = "en",
        forceRefresh: Boolean = false
    ): Result<WeatherData> {
        return refreshWeatherUseCase.execute(latitude, longitude, lang, forceRefresh)
    }

    suspend fun getCachedWeather(latitude: Double, longitude: Double): WeatherData? {
        return repository.getCachedWeather(latitude, longitude)
    }
    suspend fun getCityName(lat: Double, lon: Double, lang: String = "en"): String? {
        return repository.getCityNameFromCoordinates(lat, lon, lang)
    }
}