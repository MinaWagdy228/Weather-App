package com.example.wizzar.domain.repository

import com.example.wizzar.domain.model.Location
import com.example.wizzar.domain.model.WeatherOverview
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun observeWeather(): Flow<WeatherOverview>

    suspend fun refreshWeather(location: Location)
}