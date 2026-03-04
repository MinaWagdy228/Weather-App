package com.example.wizzar.data.dataSource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wizzar.data.dataSource.local.entity.CurrentWeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDao {


    @Query("SELECT * FROM current_weather_table LIMIT 1")
    fun observeCurrentWeather(): Flow<CurrentWeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeatherEntity: CurrentWeatherEntity)

     @Query("""
        DELETE FROM current_weather_table
        WHERE cityName = :cityName
    """)
    suspend fun deleteCurrentWeather(cityName: String)
}