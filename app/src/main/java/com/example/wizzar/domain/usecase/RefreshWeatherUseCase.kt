package com.example.wizzar.domain.usecase


import com.example.wizzar.domain.location.LocationServiceChecker
import com.example.wizzar.domain.model.DomainError
import com.example.wizzar.domain.model.Result
import com.example.wizzar.domain.model.WeatherData
import com.example.wizzar.domain.repository.WeatherRepository
import javax.inject.Inject

class RefreshWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationServiceChecker: LocationServiceChecker
) {
    suspend fun execute(lat : Double, lon : Double, forceRefresh: Boolean = false): Result<WeatherData> {
        // BUSINESS RULE 0: Check if location service is enabled
        if (!locationServiceChecker.isEnabled()) {
            return Result.Error(DomainError.LocationServiceDisabledError())
        }

        // BUSINESS RULE 2: Don't fetch if we have fresh data (< 10 minutes old)
        val cachedWeather = weatherRepository.getCachedWeather(lat, lon)
        // Bypasses the cache check if forceRefresh is true
        if (!forceRefresh && cachedWeather != null && cachedWeather.isFresh()) {
            return Result.Success(cachedWeather)
        }

        // BUSINESS RULE 3: Fetch fresh data
        return try {
            val freshWeather = weatherRepository.fetchWeatherFromApi(lat, lon)

            // BUSINESS RULE 4: Validate completeness
            if (!freshWeather.isComplete()) {
                return Result.Error(DomainError.IncompleteDataError())
            }

            // Save to cache for offline-first
            weatherRepository.saveToCache(freshWeather)
            Result.Success(freshWeather)

        } catch (_: Exception) {
            // BUSINESS RULE 5: On network error, return cached data if available
            cachedWeather?.let { Result.Success(it) }
                ?: Result.Error(DomainError.NoDataAvailableError())
        }
    }
}
