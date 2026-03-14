package com.example.wizzar.domain.usecase

import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import com.example.wizzar.domain.repository.FavoritesRepository
import com.example.wizzar.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val weatherRepository: WeatherRepository
) {
    fun observeFavorites(): Flow<List<FavoriteLocationEntity>> {
        return favoritesRepository.observeFavorites()
    }

//    // 2. The cross-repository orchestration logic for Map Pin Drops
//    suspend fun addFavoriteFromCoordinates(lat: Double, lon: Double, lang: String = "en"): Boolean {
//        return try {
//            // Step A: Get the city name from the Weather API
//            val cityName = weatherRepository.getCityNameFromCoordinates(lat, lon, lang)
//                ?: "Unknown Location" // Fallback if the network fails but we still want to save the pin
//
//            // Step B: Save it to the local database
//            favoritesRepository.addFavorite(lat, lon, cityName)
//            true // Success!
//        } catch (e: Exception) {
//            false // Failure! (The ViewModel can use this to show a Toast/Snackbar)
//        }
//    }

    suspend fun addFavoriteLocation(lat: Double, lon: Double, cityName: String) {
        favoritesRepository.addFavorite(lat, lon, cityName)
    }

    suspend fun removeFavorite(lat: Double, lon: Double) {
        favoritesRepository.removeFavorite(lat, lon)
    }
}