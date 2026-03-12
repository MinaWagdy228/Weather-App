package com.example.wizzar.core.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.wizzar.domain.model.WeatherAlert
import com.example.wizzar.domain.scheduler.WeatherAlertScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerAlertScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : WeatherAlertScheduler {

    private val workManager = WorkManager.getInstance(context)

    override fun schedule(alert: WeatherAlert) {
        // 1. Package the Domain data into WorkManager Data
        val inputData = Data.Builder()
            .putString("ALERT_ID", alert.id)
            .putLong("START_TIME", alert.startTime)
            .putLong("END_TIME", alert.endTime)
            .putBoolean("IS_ALARM", alert.isAlarmSound)
            .build()

        // 2. Set constraints (we only want to check weather if we have internet)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 3. Build the periodic request (minimum interval is 15 minutes)
        val workRequest = PeriodicWorkRequestBuilder<WeatherAlertWorker>(
            15, TimeUnit.MINUTES
        )
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag(alert.id) // Crucial: We tag it with the ID so we can find/cancel it later
            .build()

        // 4. Enqueue unique work to avoid duplicates if the user edits the alert
        workManager.enqueueUniquePeriodicWork(
            alert.id,
            ExistingPeriodicWorkPolicy.UPDATE, // Updates the existing work if ID matches
            workRequest
        )
    }

    override fun cancel(alertId: String) {
        // WorkManager makes cancellation trivial if we tagged the work properly
        workManager.cancelUniqueWork(alertId)
    }
}