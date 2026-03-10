package com.example.wizzar.di

import com.example.wizzar.data.repository.FavoritesRepositoryImpl
import com.example.wizzar.data.repository.WeatherRepositoryImpl
import com.example.wizzar.domain.repository.FavoritesRepository
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

}