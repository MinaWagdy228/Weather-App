package com.example.wizzar.data.dataSource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wizzar.data.dataSource.local.entity.CurrentWeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDao {

    @Query("SELECT * FROM current_weather_table WHERE latitude = :latitude AND longitude = :longitude LIMIT 1")
    fun observeCurrentWeather(latitude: Double, longitude: Double): Flow<CurrentWeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeatherEntity: CurrentWeatherEntity)

    @Query("DELETE FROM current_weather_table WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun deleteCurrentWeather(latitude: Double, longitude: Double)
}