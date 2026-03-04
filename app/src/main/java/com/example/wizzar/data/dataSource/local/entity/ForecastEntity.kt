package com.example.wizzar.data.dataSource.local.entity

import androidx.room.Entity

@Entity(
    tableName = "forecast_table",
    primaryKeys = ["timestamp", "cityName"]
)
data class ForecastEntity(

    val cityName: String,

    val timestamp: Long,

    val temperature: Double,

    val humidity: Int,

    val icon: String,

    val weatherId: Int
)