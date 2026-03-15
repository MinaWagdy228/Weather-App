package com.example.wizzar.domain.usecase

import com.example.wizzar.domain.location.LocationProvider
import com.example.wizzar.domain.model.Location
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val locationProvider: LocationProvider
) {
    suspend operator fun invoke(): Location {
        return locationProvider.getCurrentLocation()
    }
}

