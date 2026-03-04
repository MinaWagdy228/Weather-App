package com.example.wizzar.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing theme settings and preferences
 * Implements the repository pattern for clean architecture
 */
class ThemeRepository {

    private val _isDarkModeEnabled = MutableStateFlow(true)
    val isDarkModeEnabled: Flow<Boolean> = _isDarkModeEnabled.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        _isDarkModeEnabled.value = enabled
    }

    fun getDarkMode(): Boolean = _isDarkModeEnabled.value
}

