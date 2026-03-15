package com.example.wizzar.domain.usecase

import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import com.example.wizzar.domain.repository.FavoritesRepository
import com.example.wizzar.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    fun observeFavorites(): Flow<List<FavoriteLocationEntity>> {
        return favoritesRepository.observeFavorites()
    }

    suspend fun addFavoriteLocation(lat: Double, lon: Double, cityName: String) {
        favoritesRepository.addFavorite(lat, lon, cityName)
    }

    suspend fun removeFavorite(lat: Double, lon: Double) {
        favoritesRepository.removeFavorite(lat, lon)
    }
}