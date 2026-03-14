package com.example.wizzar.presentation.map

import com.example.wizzar.domain.model.LocationSearchResult

data class MapUiState(
    val searchQuery: String = "",
    val searchResults: List<LocationSearchResult> = emptyList(),
    val selectedLocation: LocationSearchResult? = null,
    val isLoading: Boolean = false
)