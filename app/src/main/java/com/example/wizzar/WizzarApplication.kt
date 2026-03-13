package com.example.wizzar

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WizzarApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Channel for Standard Weather Notifications
            val standardChannel = NotificationChannel(
                "weather_notification_channel",
                "Weather Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Standard alerts for weather conditions"
            }

            // 2. Channel for Loud Alarms (Bypasses Do Not Disturb if user allows)
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val alarmChannel = NotificationChannel(
                "weather_alarm_channel",
                "Weather Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Loud, full-screen alarms for severe weather"
                setSound(alarmSound, audioAttributes)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 1000, 500, 1000)
                setBypassDnd(true)
            }

            notificationManager.createNotificationChannel(standardChannel)
            notificationManager.createNotificationChannel(alarmChannel)
        }
    }
}