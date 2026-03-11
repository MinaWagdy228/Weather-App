package com.example.wizzar.presentation.home.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.data.dataSource.local.datastore.AppLanguage
import com.example.wizzar.data.dataSource.local.datastore.LocationMode
import com.example.wizzar.domain.location.LocationProvider
import com.example.wizzar.domain.model.Result
import com.example.wizzar.domain.usecase.ManageSettingsUseCase
import com.example.wizzar.domain.usecase.WeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherUseCase: WeatherUseCase,
    private val locationProvider: LocationProvider,
    private val manageSettingsUseCase: ManageSettingsUseCase
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

            val settings = manageSettingsUseCase.observeSettings().first()
            val apiLanguage = if (settings.language == AppLanguage.ARABIC) "ar" else "en"

            val targetLat: Double
            val targetLon: Double

            if (settings.locationMode == LocationMode.MAP && settings.mapLat != null && settings.mapLon != null) {
                targetLat = settings.mapLat
                targetLon = settings.mapLon
            } else {
                val loc = locationProvider.getCurrentLocation()
                if (loc.isValid()) {
                    targetLat = loc.latitude
                    targetLon = loc.longitude
                } else {
                    _uiState.value =
                        HomeUiState.Error("Could not determine current location. Ensure GPS is enabled.")
                    _isRefreshing.value = false
                    return@launch
                }
            }

            val stableLat = targetLat.roundUpToFourDecimals()
            val stableLon = targetLon.roundUpToFourDecimals()

            startObservingWeather(stableLat, stableLon)

            when (val result =
                weatherUseCase.refreshWeather(stableLat, stableLon, apiLanguage, forceRefresh)) {
                is Result.Error -> {
                    _uiEvent.emit(result.error.message)
                    if (_uiState.value is HomeUiState.Loading) {
                        _uiState.value = HomeUiState.Error(result.error.message)
                    }
                }

                is Result.Success -> Log.d("HomeViewModel", "Weather refreshed successfully")
            }

            _isRefreshing.value = false
        }
    }

    private fun startObservingWeather(lat: Double, lon: Double) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            combine(
                weatherUseCase.observeWeather(lat, lon),
                manageSettingsUseCase.observeSettings()
            ) { weatherData, settings ->
                if (weatherData != null) {
                    HomeUiState.Success(
                        currentWeather = weatherData.currentWeather,
                        hourlyForecast = weatherData.hourlyForecast,
                        dailyForecast = weatherData.dailyForecast,
                        tempUnit = settings.tempUnit,
                        windUnit = settings.windUnit
                    )
                } else {
                    if (_uiState.value !is HomeUiState.Error) {
                        HomeUiState.Loading
                    } else {
                        null
                    }
                }
            }.collect { state ->
                if (state != null) {
                    _uiState.value = state
                }
            }
        }
    }

    private fun Double.roundUpToFourDecimals(): Double {
        return round(this * 10000) / 10000.0
    }
}