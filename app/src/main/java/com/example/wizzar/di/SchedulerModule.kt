package com.example.wizzar.di

import com.example.wizzar.core.workers.WorkManagerAlertScheduler
import com.example.wizzar.domain.scheduler.WeatherAlertScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SchedulerModule {

    @Binds
    @Singleton
    abstract fun bindWeatherAlertScheduler(
        workManagerAlertScheduler: WorkManagerAlertScheduler
    ): WeatherAlertScheduler
}