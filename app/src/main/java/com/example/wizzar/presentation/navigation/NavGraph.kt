package com.example.wizzar.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wizzar.presentation.alerts.view.AlertsScreen
import com.example.wizzar.presentation.favorites.view.FavoritesScreen
import com.example.wizzar.presentation.home.view.HomeScreen
import com.example.wizzar.presentation.settings.view.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(ScreenRoutes.Home.route) {
            HomeScreen()
        }

        composable(ScreenRoutes.Favorites.route) {
            // TODO: Inject FavoritesViewModel here when ready
            FavoritesScreen()
        }

        composable(ScreenRoutes.Alerts.route) {
            // TODO: Inject AlertsViewModel here when ready
            AlertsScreen()
        }

        composable(ScreenRoutes.Settings.route) {
            // TODO: Inject SettingsViewModel here when ready
            SettingsScreen()
        }
    }
}