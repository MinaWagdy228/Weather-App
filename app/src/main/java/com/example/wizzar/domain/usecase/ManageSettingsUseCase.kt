package com.example.wizzar.domain.usecase

import com.example.wizzar.data.dataSource.local.datastore.AppLanguage
import com.example.wizzar.data.dataSource.local.datastore.LocationMode
import com.example.wizzar.data.dataSource.local.datastore.TempUnit
import com.example.wizzar.data.dataSource.local.datastore.UserSettings
import com.example.wizzar.data.dataSource.local.datastore.WindUnit
import com.example.wizzar.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    fun observeSettings(): Flow<UserSettings> {
        return settingsRepository.observeSettings()
    }

    suspend fun updateLanguage(language: AppLanguage) {
        settingsRepository.updateLanguage(language)
    }

    suspend fun updateTempUnit(unit: TempUnit) {
        settingsRepository.updateTempUnit(unit)
    }

    suspend fun updateWindUnit(unit: WindUnit) {
        settingsRepository.updateWindUnit(unit)
    }

    suspend fun updateLocationMode(mode: LocationMode, lat: Double? = null, lon: Double? = null) {
        settingsRepository.updateLocationMode(mode, lat, lon)
    }
}