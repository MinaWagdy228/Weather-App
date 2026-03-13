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
        // We only pass the ID. The Worker will fetch the freshest data from Room.
        val inputData = Data.Builder()
            .putString("ALERT_ID", alert.id)
            .build()

        // Constraints: Only run if we have an active internet connection
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<WeatherAlertWorker>(
            30, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES
        )
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag(alert.id)
            .build()

        workManager.enqueueUniquePeriodicWork(
            alert.id, // Unique name
            ExistingPeriodicWorkPolicy.UPDATE, // Update if the user changes settings
            workRequest
        )
    }

    override fun cancel(alertId: String) {
        workManager.cancelUniqueWork(alertId)
    }
}