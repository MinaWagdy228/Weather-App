package com.example.wizzar.di

import com.example.wizzar.data.dataSource.local.AlertsLocalDataSource
import com.example.wizzar.data.dataSource.local.AlertsLocalDataSourceImpl
import com.example.wizzar.data.dataSource.local.FavoritesLocalDataSource
import com.example.wizzar.data.dataSource.local.FavoritesLocalDataSourceImpl
import com.example.wizzar.data.dataSource.local.SettingsLocalDataSource
import com.example.wizzar.data.dataSource.local.SettingsLocalDataSourceImpl
import com.example.wizzar.data.dataSource.local.WeatherLocalDataSource
import com.example.wizzar.data.dataSource.local.WeatherLocalDataSourceImpl
import com.example.wizzar.data.dataSource.remote.WeatherRemoteDataSource
import com.example.wizzar.data.dataSource.remote.WeatherRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindWeatherLocalDataSource(
        weatherLocalDataSourceImpl: WeatherLocalDataSourceImpl
    ): WeatherLocalDataSource

    @Binds
    @Singleton
    abstract fun bindWeatherRemoteDataSource(
        weatherRemoteDataSourceImpl: WeatherRemoteDataSourceImpl
    ): WeatherRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFavoritesLocalDataSource(
        favoritesLocalDataSourceImpl: FavoritesLocalDataSourceImpl
    ): FavoritesLocalDataSource

    @Binds
    @Singleton
    abstract fun bindAlertsLocalDataSource(
        alertsLocalDataSourceImpl: AlertsLocalDataSourceImpl
    ): AlertsLocalDataSource

    @Binds
    @Singleton
    abstract fun bindSettingsLocalDataSource(
        settingsLocalDataSourceImpl: SettingsLocalDataSourceImpl
    ): SettingsLocalDataSource
}