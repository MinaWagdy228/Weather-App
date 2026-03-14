package com.example.wizzar.data.repository

import com.example.wizzar.data.dataSource.local.SettingsLocalDataSource
import com.example.wizzar.data.dataSource.local.datastore.AppLanguage
import com.example.wizzar.data.dataSource.local.datastore.LocationMode
import com.example.wizzar.data.dataSource.local.datastore.TempUnit
import com.example.wizzar.data.dataSource.local.datastore.UserSettings
import com.example.wizzar.data.dataSource.local.datastore.WindUnit
import com.example.wizzar.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {

    override fun observeSettings(): Flow<UserSettings> {
        return localDataSource.observeSettings()
    }

    override suspend fun updateLanguage(language: AppLanguage) {
        localDataSource.updateLanguage(language)
    }

    override suspend fun updateTempUnit(unit: TempUnit) {
        localDataSource.updateTempUnit(unit)
    }

    override suspend fun updateWindUnit(unit: WindUnit) {
        localDataSource.updateWindUnit(unit)
    }

    override suspend fun updateLocationMode(mode: LocationMode, lat: Double?, lon: Double?) {
        localDataSource.updateLocationMode(mode, lat, lon)
    }
}