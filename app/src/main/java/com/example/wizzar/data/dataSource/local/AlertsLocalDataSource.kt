package com.example.wizzar.data.dataSource.local

import com.example.wizzar.data.dataSource.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

interface AlertsLocalDataSource {
    fun observeAllAlerts(): Flow<List<AlertEntity>>
    suspend fun insertAlert(alert: AlertEntity)
    suspend fun deleteAlert(id: String)
    suspend fun getAlertById(id: String): AlertEntity?
}