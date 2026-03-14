package com.example.wizzar.data.dataSource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class AlertEntity(
    @PrimaryKey val id: String,
    val startTime: Long,
    val endTime: Long,
    val isAlarmSound: Boolean,
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val isActive: Boolean,
    val snoozedUntil: Long?,
    val lastTriggeredDate: Long?
)