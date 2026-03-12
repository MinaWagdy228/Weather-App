package com.example.wizzar.core.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result as WorkResult
import com.example.wizzar.domain.location.LocationProvider
import com.example.wizzar.domain.usecase.WeatherUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeatherAlertWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val weatherUseCase: WeatherUseCase,
    private val locationProvider: LocationProvider
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): WorkResult {
        // 1. Extract the data we packed in WorkManagerAlertScheduler
        val alertId = inputData.getString("ALERT_ID") ?: return WorkResult.failure()
        val startTime = inputData.getLong("START_TIME", 0L)
        val endTime = inputData.getLong("END_TIME", 0L)
        val isAlarm = inputData.getBoolean("IS_ALARM", false)

        val currentTime = System.currentTimeMillis()

        // 2. Enforce the user's active duration bounds
        if (currentTime < startTime) {
            return WorkResult.success() // Too early, sleep until next periodic execution
        }
        if (currentTime > endTime) {
            // It is past the active duration. We return success to finish this cycle.
            // (In a complete implementation, we'd also trigger a cancellation here)
            return WorkResult.success()
        }

        // 3. Get the user's current location to check the weather
        val location = locationProvider.getCurrentLocation()
        if (!location.isValid()) {
            return WorkResult.retry() // Retry later if we can't get a GPS fix
        }

        // 4. Fetch fresh weather data from the Domain layer
        // We use fully qualified name for your Domain Result to avoid clashing with WorkResult
        val weatherResult = weatherUseCase.refreshWeather(
            latitude = location.latitude,
            longitude = location.longitude,
            forceRefresh = true
        )

        if (weatherResult is com.example.wizzar.domain.model.Result.Success) {
            val weather = weatherResult.data.currentWeather

            // 5. Evaluate Alert Conditions
            // OpenWeatherMap condition codes: 5xx is Rain, 6xx is Snow, 7xx is Fog/Atmosphere
            val conditionId = weather.weatherConditionId
            val isRain = conditionId in 500..531
            val isSnow = conditionId in 600..622
            val isFog = conditionId == 741

            // Assuming temperature is handled by your UnitConverter, we check raw values here
            // (You might want to refine these thresholds based on the user's selected units later)
            val isExtremeTemp = weather.temperature > 40.0 || weather.temperature < 0.0
            val isHighWind = weather.wind > 15.0 // e.g., > 15 m/s

            if (isRain || isSnow || isFog || isExtremeTemp || isHighWind) {
                triggerAlert(weather.description, isAlarm)
            }

            return WorkResult.success()
        } else {
            // If the network fails, we tell WorkManager to back off and retry later
            return WorkResult.retry()
        }
    }

    private fun triggerAlert(description: String, isAlarm: Boolean) {
        // We will implement the actual Notification/Alarm Service next.
        // For now, we log it to prove our architecture pipeline works!
        Log.d("WeatherAlertWorker", "ALERT TRIGGERED: $description | Alarm Sound: $isAlarm")
    }
}