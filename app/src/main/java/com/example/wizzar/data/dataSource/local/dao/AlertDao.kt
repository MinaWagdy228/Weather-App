package com.example.wizzar.data.dataSource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wizzar.data.dataSource.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)

    @Query("DELETE FROM weather_alerts WHERE id = :id")
    suspend fun deleteAlert(id: String)

    // Exposes a reactive stream for Compose to observe instantly!
    @Query("SELECT * FROM weather_alerts")
    fun observeAllAlerts(): Flow<List<AlertEntity>>
}