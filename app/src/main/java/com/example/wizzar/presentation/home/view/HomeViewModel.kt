package com.example.wizzar.presentation.home.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.domain.location.LocationProvider
import com.example.wizzar.domain.model.Result
import com.example.wizzar.domain.usecase.WeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherUseCase: WeatherUseCase,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        fetchWeatherForCurrentLocation()
    }

    fun fetchWeatherForCurrentLocation(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isRefreshing.value = true
            val loc = locationProvider.getCurrentLocation()

            if (loc.isValid()) {
                // 👈 THE FIX: Lock coordinates to 4 decimal places (11 meters precision)
                Log.d("HomeViewModel", "Original coordinates: lat=${loc.latitude}, lon=${loc.longitude}")
                val stableLat = loc.latitude.roundUpToFourDecimals().toDouble()
                val stableLon = loc.longitude.roundUpToFourDecimals().toDouble()
                Log.d("HomeViewModel", "Stable coordinates: lat=$stableLat, lon=$stableLon")

                startObservingWeather(stableLat, stableLon)

                when (val result =
                    weatherUseCase.refreshWeather(stableLat, stableLon, forceRefresh)) {
                    is Result.Error -> {
                        Log.e("HomeViewModel", "Refresh failed: ${result.error.message}")
                        _uiEvent.emit(result.error.message)
                        if (_uiState.value is HomeUiState.Loading) {
                            _uiState.value = HomeUiState.Error(result.error.message)
                        }
                    }

                    is Result.Success -> Log.d("HomeViewModel", "Weather refreshed successfully")
                }
            } else {
                _uiState.value =
                    HomeUiState.Error("Could not determine current location. Ensure GPS is enabled.")
            }
            _isRefreshing.value = false
        }
    }

    private fun startObservingWeather(lat: Double, lon: Double) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            weatherUseCase.observeWeather(lat, lon).collect { weatherData ->
                if (weatherData != null) {
                    _uiState.value = HomeUiState.Success(
                        currentWeather = weatherData.currentWeather,
                        hourlyForecast = weatherData.hourlyForecast,
                        dailyForecast = weatherData.dailyForecast
                    )
                } else {
                    if (_uiState.value !is HomeUiState.Error) {
                        _uiState.value = HomeUiState.Loading
                    }
                }
            }
        }
    }

    fun Double.roundUpToFourDecimals(): BigDecimal {
        // Convert the Double to a BigDecimal
        val bigDecimal =
            BigDecimal(this.toString()) // Use toString() for safer conversion from Double

        // Set the scale (number of decimal places) and the rounding mode
        return bigDecimal.setScale(4, RoundingMode.CEILING)
    }
}