package com.example.wizzar.data.dataSource.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val LANGUAGE_KEY = stringPreferencesKey("language") // "en" or "ar"
    private val UNIT_KEY = stringPreferencesKey("units") // "metric" or "imperial"
    private val WIND_KEY = stringPreferencesKey("wind_speed") // "m/s" or "mph"

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            language = preferences[LANGUAGE_KEY] ?: "en",
            units = preferences[UNIT_KEY] ?: "metric",
            windSpeed = preferences[WIND_KEY] ?: "m/s"
        )
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = language }
    }

    suspend fun updateUnits(units: String) {
        context.dataStore.edit { it[UNIT_KEY] = units }
    }

    suspend fun updateWindSpeed(unit: String) {
        context.dataStore.edit { it[WIND_KEY] = unit }
    }
}

data class UserSettings(
    val language: String,
    val units: String,
    val windSpeed: String
)