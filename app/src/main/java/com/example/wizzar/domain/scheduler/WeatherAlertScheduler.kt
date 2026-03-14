package com.example.wizzar.domain.scheduler

import com.example.wizzar.domain.model.WeatherAlert

interface WeatherAlertScheduler {
    fun schedule(alert: WeatherAlert)
    fun cancel(alertId: String)
}