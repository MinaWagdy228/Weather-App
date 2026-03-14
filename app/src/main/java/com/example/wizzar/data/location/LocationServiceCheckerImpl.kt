package com.example.wizzar.data.location

import android.content.Context
import android.location.LocationManager
import com.example.wizzar.domain.location.LocationServiceChecker

class LocationServiceCheckerImpl(private val context: Context) : LocationServiceChecker {

    override fun isEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}

