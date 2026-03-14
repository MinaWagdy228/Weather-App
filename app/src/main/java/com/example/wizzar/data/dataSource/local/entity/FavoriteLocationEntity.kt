package com.example.wizzar.data.dataSource.local.entity

import androidx.room.Entity

@Entity(
    tableName = "favorite_locations_table",
    primaryKeys = ["longitude", "latitude"]
)
data class FavoriteLocationEntity(
    val longitude: Double,
    val latitude: Double,
    val cityName: String
)