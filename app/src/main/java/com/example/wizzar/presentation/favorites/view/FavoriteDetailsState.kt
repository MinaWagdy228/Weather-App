package com.example.wizzar.presentation.favorites.view

import com.example.wizzar.domain.model.WeatherData

sealed interface FavoriteDetailsState {
    object Loading : FavoriteDetailsState
    data class Success(val weatherData: WeatherData) : FavoriteDetailsState
    data class Error(val message: String) : FavoriteDetailsState
}