package com.example.wizzar.data.dataSource.local.db


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.wizzar.data.dataSource.local.dao.AlertDao
import com.example.wizzar.data.dataSource.local.dao.CurrentWeatherDao
import com.example.wizzar.data.dataSource.local.dao.FavoriteLocationDao
import com.example.wizzar.data.dataSource.local.dao.ForecastDao
import com.example.wizzar.data.dataSource.local.entity.AlertEntity
import com.example.wizzar.data.dataSource.local.entity.CurrentWeatherEntity
import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import com.example.wizzar.data.dataSource.local.entity.ForecastEntity

@Database(
    entities = [
        CurrentWeatherEntity::class,
        ForecastEntity::class,
        FavoriteLocationEntity::class,
        AlertEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun currentWeatherDao(): CurrentWeatherDao

    abstract fun forecastDao(): ForecastDao

    abstract fun favoriteLocationDao(): FavoriteLocationDao

    abstract fun alertDao(): AlertDao

}