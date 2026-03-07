package com.example.wizzar.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ScreenRoutes(val route: String, val title: String, val icon: ImageVector) {
    object Home : ScreenRoutes("home", "Home", Icons.Default.Home)
    object Favorites : ScreenRoutes("favorites", "Favorites", Icons.Default.Favorite)
    object Alerts : ScreenRoutes("alerts", "Alerts", Icons.Default.Notifications)
    object Settings : ScreenRoutes("settings", "Settings", Icons.Default.Settings)
}