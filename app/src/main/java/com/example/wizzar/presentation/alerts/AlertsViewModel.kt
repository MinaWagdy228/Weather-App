package com.example.wizzar.presentation.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.domain.model.WeatherAlert
import com.example.wizzar.domain.usecase.GetLocationUseCase
import com.example.wizzar.domain.usecase.GetWeatherUseCase
import com.example.wizzar.domain.usecase.ManageAlertsUseCase
import com.example.wizzar.domain.usecase.ManageSettingsUseCase
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

import com.example.wizzar.R
import com.example.wizzar.data.dataSource.local.datastore.LocationMode

sealed class AlertMessage {
    data class StringValue(val value: String) : AlertMessage()
    class StringResource(val resId: Int, vararg val args: Any) : AlertMessage()
}

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val manageAlertsUseCase: ManageAlertsUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
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
        onResult: (AlertMessage) -> Unit
    ) {
        viewModelScope.launch {

            val settings = manageSettingsUseCase.observeSettings().first()

            val targetLat: Double
            val targetLon: Double

            if (settings.locationMode == LocationMode.MAP && settings.mapLat != null && settings.mapLon != null) {
                targetLat = settings.mapLat
                targetLon = settings.mapLon
            } else {
                val loc = getLocationUseCase()
                if (loc.isValid()) {
                    targetLat = loc.latitude
                    targetLon = loc.longitude
                } else {
                    onResult(AlertMessage.StringResource(R.string.ensure_gps_enabled))
                    return@launch
                }
            }

            val finalLat = round(targetLat * 1000) / 1000.0
            val finalLon = round(targetLon * 1000) / 1000.0

            val cachedWeather = getWeatherUseCase.getCachedWeather(finalLat, finalLon)
            val cityName = cachedWeather?.currentWeather?.city ?: "Selected Location"

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
                isActive = true,
                snoozedUntil = null,
                lastTriggeredDate = null
            )

            manageAlertsUseCase.createAlert(newAlert)

            val timeMessage = calculateTimeUntilAlarm(startHour, startMinute)
            onResult(timeMessage)
        }
    }

    fun updateAlert(
        alert: WeatherAlert,
        startHour: Int, startMinute: Int,
        endHour: Int, endMinute: Int,
        isAlarm: Boolean,
        onResult: (AlertMessage) -> Unit
    ) {
        viewModelScope.launch {
            val startTimeInMinutes = (startHour * 60L) + startMinute
            val endTimeInMinutes = (endHour * 60L) + endMinute

            val updatedAlert = alert.copy(
                startTime = startTimeInMinutes,
                endTime = endTimeInMinutes,
                isAlarmSound = isAlarm
            )
            manageAlertsUseCase.createAlert(updatedAlert)

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

    private fun calculateTimeUntilAlarm(startHour: Int, startMinute: Int): AlertMessage {
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
            AlertMessage.StringResource(R.string.alarm_less_than_minute)
        } else {
            AlertMessage.StringResource(R.string.alarm_set_for, diffHours, diffMinutes)
        }
    }

    fun undoRemoveAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            // Re-inserts the exact same alert back into the database
            manageAlertsUseCase.createAlert(alert)
        }
    }
}