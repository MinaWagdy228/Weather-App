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
        WHERE cityName = :cityName
        ORDER BY timestamp
    """)
    fun observeForecast(cityName: String): Flow<List<ForecastEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: List<ForecastEntity>)


    @Query("""
        DELETE FROM forecast_table
        WHERE cityName = :cityName
    """)
    suspend fun deleteForecast(cityName: String)

}