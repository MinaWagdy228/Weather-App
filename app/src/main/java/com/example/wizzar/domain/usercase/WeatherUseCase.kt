package com.example.wizzar.domain.usercase

import com.example.wizzar.domain.model.Location
import com.example.wizzar.domain.repository.WeatherRepository

class WeatherUseCase(private val weatherRepo: WeatherRepository) {
    fun observeWeather() = weatherRepo.observeWeather()

    suspend fun refreshWeather(location: Location) {
        weatherRepo.refreshWeather(location)
    }
}