package com.example.wizzar.di

import android.content.Context
import androidx.room.Room
import com.example.wizzar.data.dataSource.local.dao.CurrentWeatherDao
import com.example.wizzar.data.dataSource.local.dao.ForecastDao
import com.example.wizzar.data.dataSource.local.db.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
}