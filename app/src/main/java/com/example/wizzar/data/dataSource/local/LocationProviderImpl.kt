package com.example.wizzar.data.dataSource.local

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
            try {
                // Actively fetches a fresh location with high accuracy
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).addOnSuccessListener { androidLocation ->
                    if (androidLocation != null) {
                        Log.d("LocationProvider", "Fresh Location: ${androidLocation.latitude}, ${androidLocation.longitude}")
                        continuation.resume(Location(androidLocation.latitude, androidLocation.longitude))
                    } else {
                        Log.w("LocationProvider", "Location null, falling back to Cairo")
                        continuation.resume(Location(30.0444, 31.2357))
                    }
                }.addOnFailureListener { exception ->
                    Log.e("LocationProvider", "Failed to get location", exception)
                    continuation.resume(Location(30.0444, 31.2357))
                }
            } catch (e: SecurityException) {
                Log.e("LocationProvider", "Permission missing", e)
                continuation.resume(Location(30.0444, 31.2357))
            }
        }
    }
}