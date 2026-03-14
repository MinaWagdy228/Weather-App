package com.example.wizzar.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.domain.location.LocationProvider
import com.example.wizzar.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _isDaytime = MutableStateFlow(false)
    val isDaytime: StateFlow<Boolean> = _isDaytime.asStateFlow()

    init {
        checkDaytimeForCurrentLocation()
    }

    private fun checkDaytimeForCurrentLocation() {
        viewModelScope.launch {
            val loc = locationProvider.getCurrentLocation()

            if (loc.isValid()) {
                val stableLat = loc.latitude.roundUpToThreeDecimals()
                val stableLon = loc.longitude.roundUpToThreeDecimals()

                getWeatherUseCase.observeWeather(stableLat, stableLon).collect { weatherData ->
                    if (weatherData?.currentWeather != null) {
                        val currentTime = System.currentTimeMillis() / 1000
                        val sunrise = weatherData.currentWeather.sunrise
                        val sunset = weatherData.currentWeather.sunset
                        _isDaytime.value = currentTime in sunrise..sunset
                    } else {
                        _isDaytime.value = false
                    }
                }
            }
        }
    }

    fun Double.roundUpToThreeDecimals(): Double {
        return round(this * 1000) / 1000.0
    }
}