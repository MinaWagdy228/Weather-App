package com.example.wizzar.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.data.dataSource.local.datastore.AppLanguage
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
        fetchWeatherForCurrentLocation()
    }

    fun fetchWeatherForCurrentLocation(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isRefreshing.value = true

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

            startObservingWeather(stableLat, stableLon)

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