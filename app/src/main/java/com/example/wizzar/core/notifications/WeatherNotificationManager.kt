package com.example.wizzar.core.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.wizzar.R
import com.example.wizzar.presentation.alerts.view.WeatherAlarmActivity
import com.example.wizzar.presentation.main.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WeatherNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun showStandardNotification(alertId: String, cityName: String, weatherDescription: String) {
        // 1. Intent to open the app (Home Screen) when the notification is clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 2. Build the notification
        val notification = NotificationCompat.Builder(context, "weather_notification_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Weather Alert: $cityName")
            .setContentText("Current condition: $weatherDescription. Tap to view details.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Dismiss when clicked
            .build()

        // 3. Show it (Using the alert's unique hashcode as the notification ID so they don't overwrite each other)
        try {
            NotificationManagerCompat.from(context).notify(alertId.hashCode(), notification)
        } catch (e: SecurityException) {
            // Handled if POST_NOTIFICATIONS permission is not granted on Android 13+
            Log.d("WeatherNotificationManager", "Notification permission not granted: ${e.message}")
        }
    }

    fun showFullScreenAlarm(alertId: String, cityName: String, weatherDescription: String) {
        // 1. Create the Intent to launch our custom Alarm Screen
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

        // 2. Build the High-Priority Notification
        val notification = NotificationCompat.Builder(context, "weather_alarm_channel")
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your app's icon
            .setContentTitle("SEVERE WEATHER: $cityName")
            .setContentText(weatherDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(false) // User must explicitly dismiss it
            .setOngoing(true) // Cannot be swiped away easily
            .build()

        // 3. Fire it!
        try {
            NotificationManagerCompat.from(context).notify(alertId.hashCode(), notification)
        } catch (e: SecurityException) {
            // Handle missing permissions
        }
    }
}