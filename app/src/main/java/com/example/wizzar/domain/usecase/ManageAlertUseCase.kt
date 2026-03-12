package com.example.wizzar.domain.usecase

import com.example.wizzar.domain.model.WeatherAlert
import com.example.wizzar.domain.repository.AlertsRepository
import com.example.wizzar.domain.scheduler.WeatherAlertScheduler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageAlertsUseCase @Inject constructor(
    private val repository: AlertsRepository,
    private val scheduler: WeatherAlertScheduler
) {
    fun getAlerts(): Flow<List<WeatherAlert>> {
        return repository.observeAlerts()
    }

    suspend fun createAlert(alert: WeatherAlert) {
        // 1. Save it to the local database so the UI can display it
        repository.saveAlert(alert)
        // 2. Hand it off to WorkManager to monitor in the background
        scheduler.schedule(alert)
    }

    suspend fun removeAlert(alertId: String) {
        // 1. Remove from local database
        repository.deleteAlert(alertId)
        // 2. Cancel the background worker
        scheduler.cancel(alertId)
    }
}