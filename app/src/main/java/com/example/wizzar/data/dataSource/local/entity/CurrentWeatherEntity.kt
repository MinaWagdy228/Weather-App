package com.example.wizzar.data.dataSource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather_table", primaryKeys = ["longitude", "latitude"])
data class CurrentWeatherEntity(
    val longitude: Double,
    val latitude: Double,
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
    val icon: String
)