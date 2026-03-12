package com.example.wizzar.data.repository

import com.example.wizzar.data.dataSource.local.dao.AlertDao
import com.example.wizzar.data.wrapper.toDomain
import com.example.wizzar.data.wrapper.toEntity
import com.example.wizzar.domain.model.WeatherAlert
import com.example.wizzar.domain.repository.AlertsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlertsRepositoryImpl @Inject constructor(
    private val alertDao: AlertDao
) : AlertsRepository {

    override fun observeAlerts(): Flow<List<WeatherAlert>> {
        return alertDao.observeAllAlerts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveAlert(alert: WeatherAlert) {
        alertDao.insertAlert(alert.toEntity())
    }

    override suspend fun deleteAlert(id: String) {
        alertDao.deleteAlert(id)
    }
}