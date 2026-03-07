package com.example.wizzar.data.dataSource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather_table")
data class CurrentWeatherEntity(
    @PrimaryKey(autoGenerate = false) val id: Int = 0, // Forces single row
    val cityName: String,
    val temperature: Double,
    val feelsLike: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val humidity: Int,
    val pressure: Int,
    val wind: Double,
    val description: String,
    val weatherConditionId: Int,
    val sunrise: Long,
    val sunset: Long,
    val icon: String // Added for UI
)