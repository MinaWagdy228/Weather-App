package com.example.wizzar.presentation.home.view

import com.example.wizzar.data.dataSource.local.datastore.TempUnit
import com.example.wizzar.data.dataSource.local.datastore.WindUnit
import com.example.wizzar.domain.model.CurrentWeather
import com.example.wizzar.domain.model.DailyForecast
import com.example.wizzar.domain.model.HourlyForecast

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val currentWeather: CurrentWeather,
        val hourlyForecast: List<HourlyForecast>,
        val dailyForecast: List<DailyForecast>,
        val tempUnit: TempUnit,
        val windUnit: WindUnit
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}