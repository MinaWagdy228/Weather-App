package com.example.wizzar.data.repository

import com.example.wizzar.data.dataSource.local.AlertsLocalDataSource
import com.example.wizzar.data.mapper.toDomain
import com.example.wizzar.data.mapper.toEntity
import com.example.wizzar.domain.model.WeatherAlert
import com.example.wizzar.domain.repository.AlertsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlertsRepositoryImpl @Inject constructor(
    private val localDataSource: AlertsLocalDataSource
) : AlertsRepository {

    override fun observeAlerts(): Flow<List<WeatherAlert>> {
        return localDataSource.observeAllAlerts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveAlert(alert: WeatherAlert) {
        localDataSource.insertAlert(alert.toEntity())
    }

    override suspend fun deleteAlert(id: String) {
        localDataSource.deleteAlert(id)
    }

    override suspend fun getAlertById(id: String): WeatherAlert? {
        return localDataSource.getAlertById(id)?.toDomain()
    }
}