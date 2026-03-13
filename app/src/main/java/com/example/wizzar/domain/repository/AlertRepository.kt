package com.example.wizzar.domain.repository

import com.example.wizzar.domain.model.WeatherAlert
import kotlinx.coroutines.flow.Flow

interface AlertsRepository {
    fun observeAlerts(): Flow<List<WeatherAlert>>
    suspend fun saveAlert(alert: WeatherAlert)
    suspend fun deleteAlert(id: String)
    suspend fun getAlertById(id: String): WeatherAlert?
}