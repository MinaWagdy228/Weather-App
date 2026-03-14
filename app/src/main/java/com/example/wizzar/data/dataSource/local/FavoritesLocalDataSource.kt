package com.example.wizzar.data.dataSource.local

import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow

interface FavoritesLocalDataSource {
    fun observeAllFavorites(): Flow<List<FavoriteLocationEntity>>
    suspend fun insertFavorite(entity: FavoriteLocationEntity)
    suspend fun deleteFavorite(lat: Double, lon: Double)
}