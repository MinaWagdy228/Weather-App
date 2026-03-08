package com.example.wizzar.data.dataSource.local

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.wizzar.domain.model.Location
import com.example.wizzar.domain.location.LocationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationProviderImpl @Inject constructor(private val context: Context) : LocationProvider {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location {
        return suspendCancellableCoroutine { continuation ->

            // 1. Check if we actually have permission FIRST
            val hasFineLocation = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val hasCoarseLocation = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasFineLocation && !hasCoarseLocation) {
                Log.w(
                    "LocationProvider",
                    "No permissions granted yet, falling back to default location."
                )
                continuation.resume(Location(30.0444, 31.2357))
                return@suspendCancellableCoroutine
            }

            // 2. If we DO have permission, now it's safe to ask the FusedLocationClient
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { androidLocation ->
                if (androidLocation != null) {
                    continuation.resume(
                        Location(
                            androidLocation.latitude,
                            androidLocation.longitude
                        )
                    )
                } else {
                    continuation.resume(Location(30.0444, 31.2357))
                }
            }.addOnFailureListener { exception ->
                Log.e("LocationProvider", "Failed to get location", exception)
                continuation.resume(Location(30.0444, 31.2357))
            }
        }
    }
}