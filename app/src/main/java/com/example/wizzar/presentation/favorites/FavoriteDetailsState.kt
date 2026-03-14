package com.example.wizzar.presentation.favorites

import com.example.wizzar.data.dataSource.local.datastore.TempUnit
import com.example.wizzar.data.dataSource.local.datastore.WindUnit
import com.example.wizzar.domain.model.WeatherData

sealed interface FavoriteDetailsState {
    object Loading : FavoriteDetailsState
    data class Success(val weatherData: WeatherData, val tempUnit: TempUnit, val windUnit: WindUnit) : FavoriteDetailsState
    data class Error(val message: String) : FavoriteDetailsState
}