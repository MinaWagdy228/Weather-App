package com.example.wizzar.domain.model

data class WeatherAlert(
    val id: String,
    val startTime: Long,
    val endTime: Long,
    val isAlarmSound: Boolean,

    // Location locking
    val latitude: Double,
    val longitude: Double,
    val cityName: String,

    // State management
    val isActive: Boolean = true,
    val snoozedUntil: Long? = null,
    val lastTriggeredDate: Long? = null
)