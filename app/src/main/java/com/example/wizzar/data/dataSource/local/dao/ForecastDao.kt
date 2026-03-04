package com.example.wizzar.data.dataSource.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.wizzar.data.dataSource.local.entity.ForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {

    @Query(
        """
        SELECT * FROM forecast_table
        WHERE cityName = :city
        ORDER BY timestamp ASC
    """
    )
    fun observeForecast(city: String): Flow<List<ForecastEntity>>

}