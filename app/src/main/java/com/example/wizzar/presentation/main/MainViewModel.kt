package com.example.wizzar.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.domain.usecase.WeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherUseCase: WeatherUseCase
) : ViewModel() {

    // A lightweight stream that only emits true/false based on the cached weather data
    val isDaytime: StateFlow<Boolean> = weatherUseCase.observeWeather()
        .map { weatherData ->
            if (weatherData?.currentWeather != null) {
                val currentTime = System.currentTimeMillis() / 1000
                currentTime in weatherData.currentWeather.sunrise..weatherData.currentWeather.sunset
            } else {
                false
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}