package com.example.wizzar.presentation.alerts.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.data.dataSource.local.datastore.LocationMode
import com.example.wizzar.domain.location.LocationProvider
import com.example.wizzar.domain.model.WeatherAlert
import com.example.wizzar.domain.usecase.ManageAlertsUseCase
import com.example.wizzar.domain.usecase.ManageSettingsUseCase
import com.example.wizzar.domain.usecase.WeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val manageAlertsUseCase: ManageAlertsUseCase,
    private val locationProvider: LocationProvider,
    private val weatherUseCase: WeatherUseCase,
    private val manageSettingsUseCase: ManageSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        observeAlerts()
    }

    private fun observeAlerts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            manageAlertsUseCase.getAlerts()
                .catch { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, error = exception.message ?: "Unknown Error")
                    }
                }
                .collect { alertsList ->
                    _uiState.update {
                        it.copy(isLoading = false, alerts = alertsList, error = null)
                    }
                }
        }
    }

    fun addAlert(
        startHour: Int, startMinute: Int,
        endHour: Int, endMinute: Int,
        isAlarm: Boolean,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {

            // 1. Get the active location from Settings (Synchronized with Home Screen)
            val settings = manageSettingsUseCase.observeSettings().first()

            val targetLat: Double
            val targetLon: Double

            if (settings.locationMode == LocationMode.MAP && settings.mapLat != null && settings.mapLon != null) {
                targetLat = settings.mapLat
                targetLon = settings.mapLon
            } else {
                val loc = locationProvider.getCurrentLocation()
                if (loc.isValid()) {
                    targetLat = loc.latitude
                    targetLon = loc.longitude
                } else {
                    onResult("Could not determine current location. Ensure GPS is enabled.")
                    return@launch
                }
            }

            // Round coordinates to 3 decimals to perfectly match the HomeViewModel cache logic
            val finalLat = round(targetLat * 1000) / 1000.0
            val finalLon = round(targetLon * 1000) / 1000.0

            // 2. Fetch directly from the stabilized local database via the UseCase
            val cachedWeather = weatherUseCase.getCachedWeather(finalLat, finalLon)
            val cityName = cachedWeather?.currentWeather?.city ?: "Selected Location"

            // 3. We store times as "Minutes since Midnight" (0 to 1439) for easy daily repeating math
            val startTimeInMinutes = (startHour * 60L) + startMinute
            val endTimeInMinutes = (endHour * 60L) + endMinute

            val newAlert = WeatherAlert(
                id = UUID.randomUUID().toString(),
                startTime = startTimeInMinutes,
                endTime = endTimeInMinutes,
                isAlarmSound = isAlarm,
                latitude = finalLat,
                longitude = finalLon,
                cityName = cityName,
                isActive = true, // On by default
                snoozedUntil = null,
                lastTriggeredDate = null
            )

            manageAlertsUseCase.createAlert(newAlert)

            // 4. Calculate "X hours and Y minutes left" for the Toast
            val timeMessage = calculateTimeUntilAlarm(startHour, startMinute)
            onResult(timeMessage)
        }
    }

    fun removeAlert(alertId: String) {
        viewModelScope.launch {
            manageAlertsUseCase.removeAlert(alertId)
        }
    }

    fun toggleAlertActive(alert: WeatherAlert, isActive: Boolean) {
        viewModelScope.launch {
            val updatedAlert = alert.copy(isActive = isActive)
            manageAlertsUseCase.createAlert(updatedAlert)
        }
    }

    private fun calculateTimeUntilAlarm(startHour: Int, startMinute: Int): String {
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)

        var diffHours = startHour - currentHour
        var diffMinutes = startMinute - currentMinute

        if (diffMinutes < 0) {
            diffMinutes += 60
            diffHours -= 1
        }
        if (diffHours < 0) {
            diffHours += 24 // Alarm is set for tomorrow
        }

        return if (diffHours == 0 && diffMinutes == 0) {
            "Alarm set for less than a minute from now."
        } else {
            "Alarm set for $diffHours hours and $diffMinutes minutes from now."
        }
    }
}