package com.example.wizzar.presentation.map.view

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.data.dataSource.local.datastore.LocationMode
import com.example.wizzar.domain.model.LocationSearchResult
import com.example.wizzar.domain.repository.WeatherRepository
import com.example.wizzar.domain.usecase.ManageFavoritesUseCase
import com.example.wizzar.domain.usecase.ManageSettingsUseCase
import com.example.wizzar.presentation.map.MapUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class MapViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val manageFavoritesUseCase: ManageFavoritesUseCase,
    private val manageSettingsUseCase: ManageSettingsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Retrieve the source (settings vs favorites) passed from navigation
    private val source: String = savedStateHandle["source"] ?: "favorites"

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MapUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var searchJob: Job? = null

    private fun Double.roundUpToFourDecimals(): Double {
        return round(this * 10000) / 10000.0
    }

    fun onSearchQueryChanged(query: String) {
        // Instantly update the text in the UI
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        if (query.length > 2) {
            searchJob = viewModelScope.launch {
                delay(500)
                val results = weatherRepository.searchLocations(query)
                // Update the results list in the UI
                _uiState.update { it.copy(searchResults = results) }
            }
        } else {
            _uiState.update { it.copy(searchResults = emptyList()) }
        }
    }

    fun onSearchResultClicked(result: LocationSearchResult) {
        val lockedResult = result.copy(
            latitude = result.latitude.roundUpToFourDecimals(),
            longitude = result.longitude.roundUpToFourDecimals()
        )
        val nameToDisplay = lockedResult.localizedName ?: lockedResult.name

        // Update three pieces of UI state at the exact same time!
        _uiState.update {
            it.copy(
                selectedLocation = lockedResult,
                searchQuery = nameToDisplay,
                searchResults = emptyList() // Hide the dropdown
            )
        }
    }

    fun onMapPinDropped(latitude: Double, longitude: Double, currentLanguage: String = "en") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val lat = latitude.roundUpToFourDecimals()
            val lon = longitude.roundUpToFourDecimals()

            val cityName = weatherRepository.getCityNameFromCoordinates(lat, lon, currentLanguage)
                ?: "Selected Location"

            _uiState.update {
                it.copy(
                    isLoading = false,
                    selectedLocation = LocationSearchResult(
                        name = cityName, country = "", state = null,
                        latitude = lat, longitude = lon, localizedName = cityName
                    )
                )
            }
        }
    }

    fun onConfirmLocationClicked() {
        val location = _uiState.value.selectedLocation ?: return

        viewModelScope.launch {
            if (source == "settings") {
                // Flow A: Update Global Settings
                manageSettingsUseCase.updateLocationMode(
                    LocationMode.MAP,
                    location.latitude,
                    location.longitude
                )

                _uiEvent.emit(MapUiEvent.NavigateBackWithSuccess("Home location updated!"))

            } else {
                // Flow B: Add to Favorites List
                manageFavoritesUseCase.addFavoriteLocation(
                    lat = location.latitude,
                    lon = location.longitude,
                    cityName = location.localizedName ?: location.name
                )

                _uiEvent.emit(MapUiEvent.NavigateBackWithSuccess("${location.localizedName ?: location.name} saved!"))
            }
        }
    }
}

sealed interface MapUiEvent {
    data class NavigateBackWithSuccess(val message: String) : MapUiEvent
}