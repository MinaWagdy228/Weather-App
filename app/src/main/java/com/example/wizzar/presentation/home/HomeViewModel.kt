package com.example.wizzar.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.data.dataSource.local.datastore.AppLanguage
import com.example.wizzar.data.dataSource.local.datastore.LocationMode
import com.example.wizzar.domain.usecase.GetActiveLocationUseCase
import com.example.wizzar.domain.usecase.GetWeatherUseCase
import com.example.wizzar.domain.usecase.ManageSettingsUseCase
import com.example.wizzar.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getActiveLocationUseCase: GetActiveLocationUseCase,
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
        observeSettingsChanges()
    }

    private fun observeSettingsChanges() {
        viewModelScope.launch {
            var lastLanguage: AppLanguage? = null

            // Local data class to track relevant configuration changes
            data class ConfigKey(
                val mode: LocationMode,
                val lat: Double?,
                val lon: Double?,
                val lang: AppLanguage
            )

            manageSettingsUseCase.observeSettings()
                .map { settings ->
                    ConfigKey(settings.locationMode, settings.mapLat, settings.mapLon, settings.language)
                }
                .distinctUntilChanged()
                .collect { config ->
                    // Logic:
                    // 1. If language changed -> Force Refresh (to overwrite English strings with Arabic)
                    // 2. If valid location but language same -> Normal Refresh (cache check handles freshness)
                    val shouldForce = lastLanguage != null && lastLanguage != config.lang

                    lastLanguage = config.lang

                    fetchWeatherForCurrentLocation(forceRefresh = shouldForce)
                }
        }
    }

    fun fetchWeatherForCurrentLocation(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isRefreshing.value = true

            // Logic determines whether to use GPS or Map based on current settings
            val activeLocation = getActiveLocationUseCase.execute()

            if (activeLocation == null) {
                _uiState.value = HomeUiState.Error("Could not determine current location. Ensure GPS is enabled.")
                _isRefreshing.value = false
                return@launch
            }

            val stableLat = activeLocation.latitude
            val stableLon = activeLocation.longitude

            val settings = manageSettingsUseCase.observeSettings().first()
            val apiLanguage = if (settings.language == AppLanguage.ARABIC) "ar" else "en"

            // Start observing the DB for this specific location
            startObservingWeather(stableLat, stableLon)

            // Trigger network refresh
            val result = getWeatherUseCase.refreshWeather(stableLat, stableLon, apiLanguage, forceRefresh)

            if (result is Result.Error) {
                _uiEvent.emit(result.error.message)
                if (_uiState.value is HomeUiState.Loading) {
                    _uiState.value = HomeUiState.Error(result.error.message)
                }
            }

            _isRefreshing.value = false
        }
    }

    private fun startObservingWeather(lat: Double, lon: Double) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            combine(
                getWeatherUseCase.observeWeather(lat, lon),
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
                    null
                }
            }
                .filterNotNull()
                .collect { state ->
                    _uiState.value = state
                }
        }
    }
}
