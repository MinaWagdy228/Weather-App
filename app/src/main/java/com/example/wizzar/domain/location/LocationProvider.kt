package com.example.wizzar.domain.location

import com.example.wizzar.domain.model.Location

interface LocationProvider {
    suspend fun getCurrentLocation(): Location
}