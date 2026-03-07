package com.example.wizzar.data.wrapper

import com.example.wizzar.data.dataSource.local.entity.CurrentWeatherEntity
import com.example.wizzar.data.dataSource.local.entity.ForecastEntity
import com.example.wizzar.data.dataSource.remote.dto.ForecastResponseDto
import com.example.wizzar.domain.model.CurrentWeather
import com.example.wizzar.domain.model.HourlyForecast

fun ForecastResponseDto.toCurrentWeatherEntity(): CurrentWeatherEntity {
    val current = list.first()

    return CurrentWeatherEntity(
        id = 0,
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
        icon = current.weather.first().icon
    )
}

fun CurrentWeatherEntity.toDomain(): CurrentWeather {

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
        icon = icon
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
        icon = icon
    )
}

fun ForecastResponseDto.toEntity(): List<ForecastEntity> {

    val cityName = city

    return list.map {

        ForecastEntity(
            cityName = cityName.name,

            timestamp = it.dt,

            temperature = it.main.temp,

            humidity = it.main.humidity,

            icon = it.weather.first().icon,

            weatherId = it.weather.first().id
        )

    }
}

fun ForecastEntity.toHourlyForecast(): HourlyForecast {

    return HourlyForecast(
        time = timestamp,
        temperature = temperature,
        weatherConditionId = weatherId,
        icon = icon
    )
}

// Reverse mapper: HourlyForecast to Entity
fun HourlyForecast.toEntity(cityName: String): ForecastEntity {
    return ForecastEntity(
        cityName = cityName,
        timestamp = time,
        temperature = temperature,
        humidity = 0, // Not available in HourlyForecast, use default
        icon = "", // Not available in HourlyForecast, use default
        weatherId = weatherConditionId
    )
}

