package com.example.wizzar.domain.usecase


import com.example.wizzar.domain.location.LocationServiceChecker
import com.example.wizzar.domain.util.DomainError
import com.example.wizzar.domain.util.Result
import com.example.wizzar.domain.model.WeatherData
import com.example.wizzar.domain.repository.WeatherRepository
import javax.inject.Inject

class RefreshWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationServiceChecker: LocationServiceChecker
) {
    suspend fun execute(
        lat: Double,
        lon: Double,
        lang: String = "en",
        forceRefresh: Boolean = false
    ): Result<WeatherData> {
        if (!locationServiceChecker.isEnabled()) {
            return Result.Error(DomainError.LocationServiceDisabledError())
        }

        val cachedWeather = weatherRepository.getCachedWeather(lat, lon)
        if (!forceRefresh && cachedWeather != null && cachedWeather.isFresh()) {
            return Result.Success(cachedWeather)
        }

        return try {
            val freshWeather = weatherRepository.fetchWeatherFromApi(lat, lon, lang)

            if (!freshWeather.isComplete()) {
                return Result.Error(DomainError.IncompleteDataError())
            }

            // Save to cache for offline-first
            weatherRepository.saveToCache(freshWeather)
            Result.Success(freshWeather)

        } catch (_: Exception) {
            cachedWeather?.let { Result.Success(it) }
                ?: Result.Error(DomainError.NoDataAvailableError())
        }
    }
}
