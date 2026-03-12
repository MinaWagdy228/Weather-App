package com.example.wizzar.presentation.alerts.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.domain.model.WeatherAlert
import com.example.wizzar.domain.usecase.ManageAlertsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val manageAlertsUseCase: ManageAlertsUseCase
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

    fun addAlert(startTime: Long, endTime: Long, isAlarm: Boolean) {
        viewModelScope.launch {
            val newAlert = WeatherAlert(
                id = UUID.randomUUID().toString(), // Generate a unique ID here
                startTime = startTime,
                endTime = endTime,
                isAlarmSound = isAlarm
            )
            manageAlertsUseCase.createAlert(newAlert)
        }
    }

    fun removeAlert(alertId: String) {
        viewModelScope.launch {
            manageAlertsUseCase.removeAlert(alertId)
        }
    }
}