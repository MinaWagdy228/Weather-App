package com.example.wizzar.domain.model

data class WeatherAlert(
    val id: String, // A unique identifier (UUID)
    val startTime: Long, // Start of the active duration
    val endTime: Long,   // End of the active duration
    val isAlarmSound: Boolean // True for default alarm sound, false for standard notification
)