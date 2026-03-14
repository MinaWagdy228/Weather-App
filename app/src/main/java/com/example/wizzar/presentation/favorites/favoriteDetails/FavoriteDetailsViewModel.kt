package com.example.wizzar.presentation.favorites.favoriteDetails

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.domain.usecase.GetWeatherUseCase
import com.example.wizzar.domain.usecase.ManageSettingsUseCase
import com.example.wizzar.domain.util.Result
import com.example.wizzar.presentation.favorites.favoriteDetails.FavoriteDetailsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteDetailsViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val manageSettingsUseCase: ManageSettingsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val lat: Double = savedStateHandle.get<Float>("lat")?.toDouble() ?: 0.0
    private val lon: Double = savedStateHandle.get<Float>("lon")?.toDouble() ?: 0.0

    private val _uiState = MutableStateFlow<FavoriteDetailsState>(FavoriteDetailsState.Loading)
    val uiState: StateFlow<FavoriteDetailsState> = _uiState.asStateFlow()
    private var observeJob: Job? = null

    init {
        fetchWeatherForFavorite()
    }

    private fun fetchWeatherForFavorite() {
        viewModelScope.launch {
            _uiState.value = FavoriteDetailsState.Loading
            try {
                startObservingFavoriteDetailsWeather(lat, lon)
                when (val result =
                    getWeatherUseCase.refreshWeather(lat, lon, forceRefresh = true)) {
                    is Result.Error -> {
                        Log.e("HomeViewModel", "Refresh failed: ${result.error.message}")
                        _uiState.value =
                            FavoriteDetailsState.Error("Failed to refresh weather: ${result.error.message}")
                    }

                    is Result.Success -> Log.d("HomeViewModel", "Weather refreshed successfully")
                }
            } catch (e: Exception) {
                _uiState.value =
                    FavoriteDetailsState.Error("Failed to load weather: ${e.localizedMessage}")
            }
        }
    }

    private fun startObservingFavoriteDetailsWeather(lat: Double, lon: Double) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            combine(
                getWeatherUseCase.observeWeather(lat, lon),
                manageSettingsUseCase.observeSettings()
            ) { weatherData, settings ->
                if (weatherData != null) {
                    FavoriteDetailsState.Success(
                        weatherData,
                        settings.tempUnit,
                        settings.windUnit
                    )
                } else {
                    if (_uiState.value !is FavoriteDetailsState.Error) {
                        FavoriteDetailsState.Loading
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
}