package com.example.wizzar.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result as WorkResult
import com.example.wizzar.core.notifications.WeatherNotificationManager
import com.example.wizzar.domain.repository.AlertsRepository
import com.example.wizzar.domain.usecase.GetWeatherUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

@HiltWorker
class WeatherAlertWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val alertsRepository: AlertsRepository,
    private val notificationManager: WeatherNotificationManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): WorkResult = withContext(Dispatchers.IO) {
        val alertId = inputData.getString("ALERT_ID") ?: return@withContext WorkResult.failure()

        // 1. Fetch freshest state from the Single Source of Truth
        val alert = alertsRepository.getAlertById(alertId) ?: return@withContext WorkResult.success()

        // 2. Hybrid Safety Check: If off, or if it's a Loud Alarm, ignore!
        if (!alert.isActive || alert.isAlarmSound) {
            return@withContext WorkResult.success()
        }

        val now = Calendar.getInstance()
        val currentMinutesSinceMidnight = (now.get(Calendar.HOUR_OF_DAY) * 60) + now.get(Calendar.MINUTE)
        val currentTimeMillis = System.currentTimeMillis()

        // 3. "Once a Day" Check
        if (alert.lastTriggeredDate != null) {
            val lastTriggerCal = Calendar.getInstance().apply { timeInMillis = alert.lastTriggeredDate }
            if (lastTriggerCal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
                lastTriggerCal.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                return@withContext WorkResult.success() // Already notified today
            }
        }

        // 4. Active Window Check (Handles the 11 PM to 2 AM edge case)
        val isWithinTimeframe = if (alert.startTime <= alert.endTime) {
            currentMinutesSinceMidnight in alert.startTime..alert.endTime
        } else {
            currentMinutesSinceMidnight >= alert.startTime || currentMinutesSinceMidnight <= alert.endTime
        }

        if (!isWithinTimeframe) {
            return@withContext WorkResult.success() // Outside the window, go back to sleep
        }

        // 5. Fetch Weather
        val weatherResult = getWeatherUseCase.refreshWeather(
            latitude = alert.latitude,
            longitude = alert.longitude,
            forceRefresh = true
        )

        if (weatherResult is com.example.wizzar.domain.util.Result.Success) {
            val weather = weatherResult.data.currentWeather
            val conditionId = weather.weatherConditionId

            val isBadWeather = (conditionId in 500..531) || // Rain
                    (conditionId in 600..622) || // Snow
                    (conditionId == 741) ||      // Fog
                    (weather.temperature > 40.0 || weather.temperature < 0.0) ||
                    (weather.wind > 15.0)

            if (true) {
                val description = "⚠️ SEVERE WEATHER: ${weather.description}. Check the app for details."

                notificationManager.showStandardNotification(
                    alertId = alert.id,
                    cityName = alert.cityName,
                    weatherDescription = description
                )

                // Save state so we don't spam them again today
                alertsRepository.saveAlert(alert.copy(lastTriggeredDate = currentTimeMillis))
            }

            return@withContext WorkResult.success()
        } else {
            // Network failed, ask WorkManager to retry later
            return@withContext WorkResult.retry()
        }
    }
}