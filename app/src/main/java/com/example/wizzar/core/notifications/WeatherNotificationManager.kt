package com.example.wizzar.core.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.wizzar.R
import com.example.wizzar.presentation.alerts.weatherAlarm.WeatherAlarmActivity
import com.example.wizzar.presentation.main.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WeatherNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun showStandardNotification(alertId: String, cityName: String, weatherDescription: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, "weather_notification_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Weather Alert: $cityName")
            .setContentText("Current condition: $weatherDescription. Tap to view details.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Dismiss when clicked
            .build()

        try {
            NotificationManagerCompat.from(context).notify(alertId.hashCode(), notification)
        } catch (e: SecurityException) {
            Log.d("WeatherNotificationManager", "Notification permission not granted: ${e.message}")
        }
    }

    fun showFullScreenAlarm(alertId: String, cityName: String, weatherDescription: String) {
        val fullScreenIntent = Intent(context, WeatherAlarmActivity::class.java).apply {
            putExtra("ALERT_ID", alertId)
            putExtra("CITY_NAME", cityName)
            putExtra("DESCRIPTION", weatherDescription)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            alertId.hashCode(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "weather_alarm_channel")
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your app's icon
            .setContentTitle("SEVERE WEATHER: $cityName")
            .setContentText(weatherDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(alertId.hashCode(), notification)
        } catch (e: SecurityException) {
            // Handle missing permissions
        }
    }
}