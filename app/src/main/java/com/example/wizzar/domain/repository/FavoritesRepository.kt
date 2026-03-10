package com.example.wizzar.domain.repository

import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    // Returns a Flow so the UI instantly updates when a favorite is added or swiped away
    fun observeFavorites(): Flow<List<FavoriteLocationEntity>>

    suspend fun addFavorite(lat: Double, lon: Double, cityName: String)

    suspend fun removeFavorite(lat: Double, lon: Double)
}