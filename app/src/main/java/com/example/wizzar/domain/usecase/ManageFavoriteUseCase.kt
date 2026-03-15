package com.example.wizzar.domain.usecase

import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import com.example.wizzar.domain.repository.FavoritesRepository
import com.example.wizzar.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ManageFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val weatherRepository: WeatherRepository
) {
    fun observeFavorites(): Flow<List<FavoriteLocationEntity>> {
        return favoritesRepository.observeFavorites()
    }

    suspend fun refreshFavoriteCityNames(language: String) {
        val favorites = favoritesRepository.observeFavorites().first()
        favorites.forEach { favorite ->
            try {
                val newName = weatherRepository.getCityNameFromCoordinates(
                    favorite.latitude,
                    favorite.longitude,
                    language
                )
                if (!newName.isNullOrBlank()) {
                    favoritesRepository.addFavorite(
                        favorite.latitude,
                        favorite.longitude,
                        newName
                    )
                }
            } catch (e: Exception) {
                // Ignore errors
            }
        }
    }

    suspend fun addFavoriteLocation(lat: Double, lon: Double, cityName: String) {
        favoritesRepository.addFavorite(lat, lon, cityName)
    }

    suspend fun removeFavorite(lat: Double, lon: Double) {
        favoritesRepository.removeFavorite(lat, lon)
    }
}