package com.example.wizzar.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.wizzar.core.notifications.WeatherNotificationManager
import com.example.wizzar.di.ExactAlarmScheduler
import com.example.wizzar.domain.repository.AlertsRepository
import com.example.wizzar.domain.scheduler.WeatherAlertScheduler
import com.example.wizzar.domain.usecase.WeatherUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WeatherAlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var weatherUseCase: WeatherUseCase
    @Inject lateinit var alertsRepository: AlertsRepository
    @Inject lateinit var notificationManager: WeatherNotificationManager

    @Inject
    @ExactAlarmScheduler
    lateinit var exactAlarmScheduler: WeatherAlertScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getStringExtra("ALERT_ID") ?: return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val alert = alertsRepository.getAlertById(alertId) ?: return@launch

                if (!alert.isActive) return@launch

                if (!alert.isAlarmSound) return@launch

                val weatherResult = weatherUseCase.refreshWeather(
                    latitude = alert.latitude,
                    longitude = alert.longitude,
                    forceRefresh = true
                )

                var finalDescription = "Unable to fetch current weather data."

                // 3. Evaluate Good vs Bad Weather unconditionally
                if (weatherResult is com.example.wizzar.domain.model.Result.Success) {
                    val weather = weatherResult.data.currentWeather
                    val conditionId = weather.weatherConditionId

                    val isBadWeather = (conditionId in 500..531) || // Rain
                            (conditionId in 600..622) || // Snow
                            (conditionId == 741) ||      // Fog
                            (weather.temperature > 40.0 || weather.temperature < 0.0) ||
                            (weather.wind > 15.0)

                    finalDescription = if (isBadWeather) {
                        "⚠️ SEVERE WEATHER: ${weather.description}. Stay safe!"
                    } else {
                        "✅ Conditions are good: ${weather.description}. Have a great day!"
                    }
                }

                // 4. ALWAYS fire the Full Screen Activity (as per your exact alarm requirement)
                notificationManager.showFullScreenAlarm(
                    alertId = alert.id,
                    cityName = alert.cityName,
                    weatherDescription = finalDescription
                )

                // 5. Reschedule the exact alarm for tomorrow!
                // Because the current time is now >= the start time, the scheduler's math
                // will automatically add(Calendar.DAY_OF_YEAR, 1) to push it to tomorrow.
                exactAlarmScheduler.schedule(alert)

            } catch (e: Exception) {
                Log.e("WeatherAlarmReceiver", "Error processing exact alarm", e)
            } finally {
                // Must call finish() so the OS knows we are done and can sleep
                pendingResult.finish()
            }
        }
    }
}