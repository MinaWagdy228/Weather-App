package com.example.wizzar.data.dataSource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class AlertEntity(
    @PrimaryKey val id: String, // Matches the UUID from the Domain model
    val startTime: Long,
    val endTime: Long,
    val isAlarmSound: Boolean
)