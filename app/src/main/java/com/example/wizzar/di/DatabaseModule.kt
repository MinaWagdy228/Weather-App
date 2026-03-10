package com.example.wizzar.di

import android.content.Context
import androidx.room.Room
import com.example.wizzar.data.dataSource.local.LocationProviderImpl
import com.example.wizzar.data.dataSource.local.dao.CurrentWeatherDao
import com.example.wizzar.data.dataSource.local.dao.FavoriteLocationDao
import com.example.wizzar.data.dataSource.local.dao.ForecastDao
import com.example.wizzar.data.dataSource.local.db.WeatherDatabase
import com.example.wizzar.domain.location.LocationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.wizzar.data.location.LocationServiceCheckerImpl
import com.example.wizzar.domain.location.LocationServiceChecker

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWeatherDatabase(
        @ApplicationContext context: Context
    ): WeatherDatabase {

        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_database"
        ).build()
    }

    @Provides
    fun provideForecastDao(
        database: WeatherDatabase
    ): ForecastDao {
        return database.forecastDao()
    }

    @Provides
    fun provideCurrentWeatherDao(
        database: WeatherDatabase
    ): CurrentWeatherDao {
        return database.currentWeatherDao()
    }

    @Provides
    @Singleton
    fun provideLocationProvider(
        @ApplicationContext context: Context
    ): LocationProvider {
        return LocationProviderImpl(context)
    }

    @Provides
    @Singleton
    fun provideLocationServiceChecker(
        @ApplicationContext context: Context
    ): LocationServiceChecker {
        return LocationServiceCheckerImpl(context)
    }

    @Provides
    fun provideFavoriteLocationDao(
        database: WeatherDatabase
    ): FavoriteLocationDao {
        return database.favoriteLocationDao()
    }
}