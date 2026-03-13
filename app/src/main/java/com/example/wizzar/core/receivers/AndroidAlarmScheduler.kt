package com.example.wizzar.core.receivers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.wizzar.domain.model.WeatherAlert
import com.example.wizzar.domain.scheduler.WeatherAlertScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject

class AndroidAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : WeatherAlertScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(alert: WeatherAlert) {
        val intent = Intent(context, WeatherAlarmReceiver::class.java).apply {
            putExtra("ALERT_ID", alert.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            val currentHour = get(Calendar.HOUR_OF_DAY)
            val currentMinute = get(Calendar.MINUTE)
            val currentTotalMinutes = (currentHour * 60) + currentMinute

            set(Calendar.HOUR_OF_DAY, (alert.startTime / 60).toInt())
            set(Calendar.MINUTE, (alert.startTime % 60).toInt())
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (alert.startTime <= currentTotalMinutes) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    // We have permission, fire it exactly!
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    Log.w("AndroidAlarmScheduler", "Exact alarm permission denied. Falling back to inexact.")
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e("AndroidAlarmScheduler", "Failed to schedule alarm", e)
        }
    }

    override fun cancel(alertId: String) {
        val intent = Intent(context, WeatherAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alertId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}