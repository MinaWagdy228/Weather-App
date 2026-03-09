package com.example.wizzar.data.dataSource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wizzar.data.dataSource.local.entity.ForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {

    @Query("""
        SELECT * FROM forecast_table
        WHERE longitude = :longitude AND latitude = :latitude
        ORDER BY timestamp
    """)
    fun observeForecast(latitude: Double, longitude: Double): Flow<List<ForecastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: List<ForecastEntity>)

    @Query("DELETE FROM forecast_table WHERE longitude = :longitude AND latitude = :latitude")
    suspend fun deleteForecast(latitude: Double, longitude: Double)

}