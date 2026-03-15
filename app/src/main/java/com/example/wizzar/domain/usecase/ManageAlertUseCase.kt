package com.example.wizzar.domain.usecase

import com.example.wizzar.di.ExactAlarmScheduler
import com.example.wizzar.di.WindowNotificationScheduler
import com.example.wizzar.domain.model.WeatherAlert
import com.example.wizzar.domain.repository.AlertsRepository
import com.example.wizzar.domain.scheduler.WeatherAlertScheduler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageAlertsUseCase @Inject constructor(
    private val repository: AlertsRepository,
    @ExactAlarmScheduler private val exactAlarmScheduler: WeatherAlertScheduler,
    @WindowNotificationScheduler private val windowNotificationScheduler: WeatherAlertScheduler
) {
    fun getAlerts(): Flow<List<WeatherAlert>> {
        return repository.observeAlerts()
    }

    suspend fun createAlert(alert: WeatherAlert) {
        repository.saveAlert(alert)

        exactAlarmScheduler.cancel(alert.id)
        windowNotificationScheduler.cancel(alert.id)

        // Route to the appropriate execution engine
        if (alert.isAlarmSound) {
            exactAlarmScheduler.schedule(alert)
        } else {
            windowNotificationScheduler.schedule(alert)
        }
    }

    suspend fun removeAlert(alertId: String) {
        repository.deleteAlert(alertId)

        exactAlarmScheduler.cancel(alertId)
        windowNotificationScheduler.cancel(alertId)
    }

    suspend fun snoozeAlert(alertId: String) {
        val alert = repository.getAlertById(alertId) ?: return
        val currentTimeMillis = System.currentTimeMillis()

        // Add 10 minutes to the snooze timer
        val updatedAlert = alert.copy(snoozedUntil = currentTimeMillis + (10 * 60 * 1000))

        // Re-save (which also updates the schedulers via our existing logic)
        createAlert(updatedAlert)
    }

    suspend fun dismissAlertForToday(alertId: String) {
        val alert = repository.getAlertById(alertId) ?: return
        val currentTimeMillis = System.currentTimeMillis()

        // Stamp today's date so it skips until tomorrow
        val updatedAlert = alert.copy(lastTriggeredDate = currentTimeMillis)

        createAlert(updatedAlert)
    }
}