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

    val icon: String, // not needed, as we can get the icon from the weather condition id, but it is easier to store it here

    val weatherId: Int
)