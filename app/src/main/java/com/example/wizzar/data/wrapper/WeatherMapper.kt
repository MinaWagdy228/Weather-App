package com.example.wizzar.data.wrapper

import com.example.wizzar.data.dataSource.local.entity.CurrentWeatherEntity
import com.example.wizzar.data.dataSource.local.entity.ForecastEntity
import com.example.wizzar.data.dataSource.remote.dto.CurrentWeatherResponseDto
import com.example.wizzar.data.dataSource.remote.dto.ForecastResponseDto
import com.example.wizzar.domain.model.HourlyForecast

fun CurrentWeatherResponseDto.toEntity(): CurrentWeatherEntity {

    return CurrentWeatherEntity(
        cityName = name,

        temperature = main.temp,
        feelsLike = main.feelsLike,

        minTemp = main.tempMin,
        maxTemp = main.tempMax,

        humidity = main.humidity,
        pressure = main.pressure,

        wind = wind.speed,

        description = weather.first().description,
        icon = weather.first().icon,

        sunrise = sys.sunrise,
        sunset = sys.sunset
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
        weatherConditionId = weatherId
    )
}