package com.example.wizzar.presentation.alerts.view

import com.example.wizzar.domain.model.WeatherAlert

data class AlertsUiState(
    val isLoading: Boolean = true,
    val alerts: List<WeatherAlert> = emptyList(),
    val error: String? = null
)