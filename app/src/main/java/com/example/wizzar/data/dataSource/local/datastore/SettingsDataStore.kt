package com.example.wizzar.data.dataSource.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

enum class LocationMode { GPS, MAP }
enum class TempUnit { CELSIUS, KELVIN, FAHRENHEIT }
enum class WindUnit { METER_SEC, MILE_HOUR }
enum class AppLanguage { ENGLISH, ARABIC, DEFAULT }

data class UserSettings(
    val language: AppLanguage,
    val tempUnit: TempUnit,
    val windUnit: WindUnit,
    val locationMode: LocationMode,
    val mapLat: Double?,
    val mapLon: Double?
)

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Keys
    private val LANGUAGE_KEY = stringPreferencesKey("language")
    private val TEMP_UNIT_KEY = stringPreferencesKey("temp_unit")
    private val WIND_UNIT_KEY = stringPreferencesKey("wind_unit")
    private val LOCATION_MODE_KEY = stringPreferencesKey("location_mode")
    private val MAP_LAT_KEY = doublePreferencesKey("map_lat")
    private val MAP_LON_KEY = doublePreferencesKey("map_lon")

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            language = AppLanguage.valueOf(preferences[LANGUAGE_KEY] ?: AppLanguage.DEFAULT.name),
            tempUnit = TempUnit.valueOf(preferences[TEMP_UNIT_KEY] ?: TempUnit.CELSIUS.name),
            windUnit = WindUnit.valueOf(preferences[WIND_UNIT_KEY] ?: WindUnit.METER_SEC.name),
            locationMode = LocationMode.valueOf(preferences[LOCATION_MODE_KEY] ?: LocationMode.GPS.name),
            mapLat = preferences[MAP_LAT_KEY],
            mapLon = preferences[MAP_LON_KEY]
        )
    }

    suspend fun updateLanguage(language: AppLanguage) {
        context.dataStore.edit { it[LANGUAGE_KEY] = language.name }
    }

    suspend fun updateTempUnit(unit: TempUnit) {
        context.dataStore.edit { it[TEMP_UNIT_KEY] = unit.name }
    }

    suspend fun updateWindUnit(unit: WindUnit) {
        context.dataStore.edit { it[WIND_UNIT_KEY] = unit.name }
    }

    suspend fun updateLocationMode(mode: LocationMode, lat: Double? = null, lon: Double? = null) {
        context.dataStore.edit { preferences ->
            preferences[LOCATION_MODE_KEY] = mode.name
            if (lat != null && lon != null) {
                preferences[MAP_LAT_KEY] = lat
                preferences[MAP_LON_KEY] = lon
            }
        }
    }
}