package com.example.wizzar.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.wizzar.presentation.alerts.AlertsScreen
import com.example.wizzar.presentation.favorites.FavoriteDetailsScreen
import com.example.wizzar.presentation.favorites.FavoritesScreen
import com.example.wizzar.presentation.home.HomeScreen
import com.example.wizzar.presentation.map.MapScreen
import com.example.wizzar.presentation.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController, paddingValues: PaddingValues
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
            FavoritesScreen(onNavigateToMap = {
                navController.navigate(ScreenRoutes.Map.createRoute("favorites"))
            }, onNavigateToDetails = { lat, lon ->
                navController.navigate(ScreenRoutes.FavoriteDetails.createRoute(lat, lon))
            })
        }

        composable(ScreenRoutes.Alerts.route) {
            AlertsScreen()
        }

        composable(ScreenRoutes.Settings.route) {
            SettingsScreen(
                onNavigateToMap = {
                    navController.navigate(ScreenRoutes.Map.createRoute("settings"))
                }
            )
        }
        composable(
            route = ScreenRoutes.Map.route,
            arguments = listOf(navArgument("source") { defaultValue = "favorites" })
        ) {
            MapScreen(
                onNavigateBack = { message ->
                    // Optionally, you could pass this message back to Favorites via SavedStateHandle,
                    // but popping the stack is all we need to return to the updated list!
                    navController.popBackStack()
                })
        }

        composable(
            route = ScreenRoutes.FavoriteDetails.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lon") { type = NavType.FloatType })
        ) {
            // The ViewModel automatically catches the arguments from here via SavedStateHandle!
            FavoriteDetailsScreen(
                onBackClick = { navController.popBackStack() })
        }
    }
}