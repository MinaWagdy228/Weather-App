package com.example.wizzar.data.wrapper

import com.example.wizzar.data.dataSource.local.entity.CurrentWeatherEntity
import com.example.wizzar.data.dataSource.local.entity.ForecastEntity
import com.example.wizzar.data.dataSource.remote.dto.ForecastResponseDto
import com.example.wizzar.domain.model.CurrentWeather
import com.example.wizzar.domain.model.HourlyForecast

fun ForecastResponseDto.toCurrentWeatherEntity(requestedLatitude : Double, requestedLongitude : Double): CurrentWeatherEntity {
    val current = list.first()

    return CurrentWeatherEntity(
        cityName = city.name,
        temperature = current.main.temp,
        feelsLike = current.main.feelsLike,
        minTemp = current.main.tempMin,
        maxTemp = current.main.tempMax,
        humidity = current.main.humidity,
        pressure = current.main.pressure,
        wind = current.wind.speed,
        description = current.weather.first().description,
        weatherConditionId = current.weather.first().id,
        sunrise = city.sunrise,
        sunset = city.sunset,
        icon = current.weather.first().icon,
        longitude = requestedLongitude,
        latitude = requestedLatitude
    )
}

fun CurrentWeatherEntity?.toDomain(): CurrentWeather? {

    // 2. We safely catch the null from the empty Room database here
    if (this == null) return null

    return CurrentWeather(
        city = cityName,
        temperature = temperature,
        feelsLike = feelsLike,
        minTemp = minTemp,
        maxTemp = maxTemp,
        humidity = humidity,
        pressure = pressure,
        wind = wind,
        description = description,
        weatherConditionId = weatherConditionId,
        sunrise = sunrise,
        sunset = sunset,
        icon = icon,
        longitude = longitude,
        latitude = latitude
    )
}

// Reverse mapper: Domain to Entity
fun CurrentWeather.toEntity(): CurrentWeatherEntity {
    return CurrentWeatherEntity(
        cityName = city,
        temperature = temperature,
        feelsLike = feelsLike,
        minTemp = minTemp,
        maxTemp = maxTemp,
        humidity = humidity,
        pressure = pressure,
        wind = wind,
        description = description,
        weatherConditionId = weatherConditionId,
        sunrise = sunrise,
        sunset = sunset,
        icon = icon,
        longitude = longitude,
        latitude = latitude
    )
}

fun ForecastResponseDto.toEntity(requestedLatitude : Double, requestedLongitude : Double): List<ForecastEntity> {
    val cityName = city

    return list.map {
        ForecastEntity(
            cityName = cityName.name,
            timestamp = it.dt,
            temperature = it.main.temp,
            humidity = it.main.humidity,
            icon = it.weather.first().icon,
            weatherId = it.weather.first().id,
            longitude = requestedLongitude,
            latitude = requestedLatitude
        )
    }
}

fun ForecastEntity.toHourlyForecast(): HourlyForecast {
    return HourlyForecast(
        time = timestamp,
        temperature = temperature,
        weatherConditionId = weatherId,
        icon = icon,
        longitude = longitude,
        latitude = latitude
    )
}

// Reverse mapper: HourlyForecast to Entity
fun HourlyForecast.toEntity(cityName: String, latitude : Double, longitude: Double): ForecastEntity {
    return ForecastEntity(
        cityName = cityName,
        timestamp = time,
        temperature = temperature,
        humidity = 0,
        icon = "",
        weatherId = weatherConditionId,
        longitude = longitude,
        latitude = latitude
    )
}