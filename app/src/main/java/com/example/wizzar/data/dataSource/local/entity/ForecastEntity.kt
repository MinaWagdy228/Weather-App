package com.example.wizzar.data.dataSource.local.entity

import androidx.room.Entity

@Entity(
    tableName = "forecast_table",
    primaryKeys = ["timestamp", "longitude", "latitude"]
)
data class ForecastEntity(
    val longitude: Double,
    val latitude: Double,
    val cityName: String,

    val timestamp: Long,

    val temperature: Double,

    val humidity: Int,

    val icon: String,

    val weatherId: Int
)