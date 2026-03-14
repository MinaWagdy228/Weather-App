package com.example.wizzar.core.sheduler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.wizzar.core.receivers.WeatherAlarmReceiver
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

        // 1. Calculate the actual End Time for "Today" to validate the Snooze
        // We need this to ensure a 10-minute snooze doesn't push us past the allowed window (e.g., 5:00 PM)
        val todayEndTimeInMillis = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, (alert.endTime / 60).toInt())
            set(Calendar.MINUTE, (alert.endTime % 60).toInt())
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // 2. Determine if the snooze acts as a valid trigger
        // It must be: Not Null AND In the Future AND Before the End Time
        val isValidSnooze = alert.snoozedUntil != null &&
                alert.snoozedUntil > System.currentTimeMillis() &&
                alert.snoozedUntil < todayEndTimeInMillis

        val triggerTimeInMillis: Long = if (isValidSnooze) {
            alert.snoozedUntil
        } else {
            // Fallback: If snooze is invalid (e.g. past end time), we schedule for the standard start time.
            val calendar = Calendar.getInstance().apply {
                val currentHour = get(Calendar.HOUR_OF_DAY)
                val currentMinute = get(Calendar.MINUTE)
                val currentTotalMinutes = (currentHour * 60) + currentMinute

                set(Calendar.HOUR_OF_DAY, (alert.startTime / 60).toInt())
                set(Calendar.MINUTE, (alert.startTime % 60).toInt())
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // If start time has passed for today (which is true if we just rejected a snooze because it was too late),
                // schedule for tomorrow.
                if (alert.startTime <= currentTotalMinutes) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            calendar.timeInMillis
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTimeInMillis,
                        pendingIntent
                    )
                } else {
                    Log.w("AndroidAlarmScheduler", "Exact alarm permission denied. Falling back to inexact.")
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTimeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeInMillis,
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