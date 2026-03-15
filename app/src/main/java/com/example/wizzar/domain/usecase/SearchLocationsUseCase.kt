package com.example.wizzar.domain.usecase

import com.example.wizzar.domain.model.LocationSearchResult
import com.example.wizzar.domain.repository.WeatherRepository
import javax.inject.Inject

class SearchLocationsUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend fun search(query: String): List<LocationSearchResult> {
        return repository.searchLocations(query)
    }

    suspend fun getCityName(lat: Double, lon: Double, lang: String): String? {
        return repository.getCityNameFromCoordinates(lat, lon, lang)
    }
}

