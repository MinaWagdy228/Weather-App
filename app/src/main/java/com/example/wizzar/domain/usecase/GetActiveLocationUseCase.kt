package com.example.wizzar.domain.usecase

import com.example.wizzar.data.dataSource.local.datastore.LocationMode
import com.example.wizzar.domain.location.LocationProvider
import com.example.wizzar.domain.model.Location
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.math.round

class GetActiveLocationUseCase @Inject constructor(
    private val locationProvider: LocationProvider,
    private val manageSettingsUseCase: ManageSettingsUseCase
) {
    suspend fun execute(): Location? {
        val settings = manageSettingsUseCase.observeSettings().first()

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
                return null
            }
        }

        val stableLat = round(targetLat * 1000) / 1000.0
        val stableLon = round(targetLon * 1000) / 1000.0

        return Location(stableLat, stableLon)
    }
}