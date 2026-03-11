package com.example.wizzar.domain.repository

import com.example.wizzar.data.dataSource.local.datastore.AppLanguage
import com.example.wizzar.data.dataSource.local.datastore.LocationMode
import com.example.wizzar.data.dataSource.local.datastore.TempUnit
import com.example.wizzar.data.dataSource.local.datastore.UserSettings
import com.example.wizzar.data.dataSource.local.datastore.WindUnit
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<UserSettings>

    suspend fun updateLanguage(language: AppLanguage)
    suspend fun updateTempUnit(unit: TempUnit)
    suspend fun updateWindUnit(unit: WindUnit)
    suspend fun updateLocationMode(mode: LocationMode, lat: Double? = null, lon: Double? = null)
}