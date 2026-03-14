package com.example.wizzar.data.repository

import com.example.wizzar.data.dataSource.local.FavoritesLocalDataSource
import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import com.example.wizzar.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val localDataSource: FavoritesLocalDataSource
) : FavoritesRepository {

    override fun observeFavorites(): Flow<List<FavoriteLocationEntity>> {
        return localDataSource.observeAllFavorites()
    }

    override suspend fun addFavorite(lat: Double, lon: Double, cityName: String) {
        val entity = FavoriteLocationEntity(
            latitude = lat,
            longitude = lon,
            cityName = cityName
        )
        localDataSource.insertFavorite(entity)
    }

    override suspend fun removeFavorite(lat: Double, lon: Double) {
        localDataSource.deleteFavorite(lat, lon)
    }
}