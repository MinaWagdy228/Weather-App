package com.example.wizzar.data.dataSource.local

import com.example.wizzar.data.dataSource.local.dao.AlertDao
import com.example.wizzar.data.dataSource.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlertsLocalDataSourceImpl @Inject constructor(
    private val alertDao: AlertDao
) : AlertsLocalDataSource {

    override fun observeAllAlerts(): Flow<List<AlertEntity>> {
        return alertDao.observeAllAlerts()
    }

    override suspend fun insertAlert(alert: AlertEntity) {
        alertDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(id: String) {
        alertDao.deleteAlert(id)
    }

    override suspend fun getAlertById(id: String): AlertEntity? {
        return alertDao.getAlertById(id)
    }
}