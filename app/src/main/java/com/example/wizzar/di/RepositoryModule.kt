package com.example.wizzar.di

import com.example.wizzar.data.repository.AlertsRepositoryImpl
import com.example.wizzar.data.repository.FavoritesRepositoryImpl
import com.example.wizzar.data.repository.SettingsRepositoryImpl
import com.example.wizzar.data.repository.WeatherRepositoryImpl
import com.example.wizzar.domain.repository.AlertsRepository
import com.example.wizzar.domain.repository.FavoritesRepository
import com.example.wizzar.domain.repository.SettingsRepository
import com.example.wizzar.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(
        favoritesRepositoryImpl: FavoritesRepositoryImpl
    ): FavoritesRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindAlertsRepository(
        alertsRepositoryImpl: AlertsRepositoryImpl
    ): AlertsRepository
}