package com.example.wizzar.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.domain.location.LocationProvider
import com.example.wizzar.domain.usecase.WeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherUseCase: WeatherUseCase,
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
                // 👈 THE FIX: Apply the exact same lock!
                val stableLat = loc.latitude.roundUpToFourDecimals().toDouble()
                val stableLon = loc.longitude.roundUpToFourDecimals().toDouble()

                weatherUseCase.observeWeather(stableLat, stableLon).collect { weatherData ->
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

    fun Double.roundUpToFourDecimals(): Double {
        return round(this * 10000) / 10000.0
    }
}