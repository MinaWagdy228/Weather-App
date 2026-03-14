package com.example.wizzar.data.dataSource.local

import com.example.wizzar.data.dataSource.local.datastore.AppLanguage
import com.example.wizzar.data.dataSource.local.datastore.LocationMode
import com.example.wizzar.data.dataSource.local.datastore.SettingsDataStore
import com.example.wizzar.data.dataSource.local.datastore.TempUnit
import com.example.wizzar.data.dataSource.local.datastore.UserSettings
import com.example.wizzar.data.dataSource.local.datastore.WindUnit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsLocalDataSourceImpl @Inject constructor(
    private val dataStore: SettingsDataStore
) : SettingsLocalDataSource {

    override fun observeSettings(): Flow<UserSettings> {
        return dataStore.settingsFlow
    }

    override suspend fun updateLanguage(language: AppLanguage) {
        dataStore.updateLanguage(language)
    }

    override suspend fun updateTempUnit(unit: TempUnit) {
        dataStore.updateTempUnit(unit)
    }

    override suspend fun updateWindUnit(unit: WindUnit) {
        dataStore.updateWindUnit(unit)
    }

    override suspend fun updateLocationMode(mode: LocationMode, lat: Double?, lon: Double?) {
        dataStore.updateLocationMode(mode, lat, lon)
    }
}