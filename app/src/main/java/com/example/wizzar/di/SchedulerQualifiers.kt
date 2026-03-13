package com.example.wizzar.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExactAlarmScheduler

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WindowNotificationScheduler