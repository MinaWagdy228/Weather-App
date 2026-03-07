package com.example.wizzar.presentation.home.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.domain.usecase.WeatherUseCase
import com.example.wizzar.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherUseCase: WeatherUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        fetchWeatherForCurrentLocation()
    }

    fun fetchWeatherForCurrentLocation(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isRefreshing.value = true

            when (val result = weatherUseCase.refreshWeather(forceRefresh)) {
                is Result.Error -> {
                    Log.e("HomeViewModel", "Refresh failed: ${result.error.message}")
                    // 2. Emit the error message so the UI can show it in a Toast!
                    _uiEvent.emit(result.error.message)
                }
                is Result.Success -> {
                    Log.d("HomeViewModel", "Weather refreshed successfully")
                }
            }

            _isRefreshing.value = false
        }
    }

    val uiState: StateFlow<HomeUiState> = weatherUseCase.observeWeather()
        .map { weatherData ->
            if (weatherData != null && weatherData.currentWeather != null) {
                HomeUiState.Success(
                    currentWeather = weatherData.currentWeather,
                    hourlyForecast = weatherData.hourlyForecast,
                    dailyForecast = weatherData.dailyForecast
                )
            } else {
                HomeUiState.Loading
            }
        }
        .catch { exception ->
            emit(HomeUiState.Error(exception.localizedMessage ?: "An unexpected error occurred"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )
}