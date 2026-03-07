package com.example.wizzar.domain.model

data class WeatherData(
    val currentWeather: CurrentWeather,
    val hourlyForecast: List<HourlyForecast>,
    val dailyForecast: List<DailyForecast>,
    val fetchedAt: Long = System.currentTimeMillis()
) {
    fun isFresh(maxAgeMinutes: Int = 10): Boolean {
        val ageInMillis = System.currentTimeMillis() - fetchedAt
        val ageInMinutes = ageInMillis / (1000 * 60)
        return ageInMinutes < maxAgeMinutes
    }

    fun isComplete(): Boolean {
        return currentWeather.city.isNotBlank() &&
               currentWeather.temperature > 0 &&
               hourlyForecast.isNotEmpty() &&
               dailyForecast.isNotEmpty()
    }
}

