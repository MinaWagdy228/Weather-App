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
import com.example.wizzar.presentation.alerts.view.AlertsScreen
import com.example.wizzar.presentation.favorites.view.FavoriteDetailsScreen
import com.example.wizzar.presentation.favorites.view.FavoritesScreen
import com.example.wizzar.presentation.home.view.HomeScreen
import com.example.wizzar.presentation.map.MapScreen
import com.example.wizzar.presentation.settings.view.SettingsScreen

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

        // UPDATED: Connected the Favorites Screen navigation lambdas
        composable(ScreenRoutes.Favorites.route) {
            FavoritesScreen(onNavigateToMap = {
                navController.navigate(ScreenRoutes.Map.route)
            }, onNavigateToDetails = { lat, lon ->
                navController.navigate(ScreenRoutes.FavoriteDetails.createRoute(lat, lon))
            })
        }

        composable(ScreenRoutes.Alerts.route) {
            AlertsScreen()
        }

        composable(ScreenRoutes.Settings.route) {
            SettingsScreen()
        }

        // NEW: The Map Screen
        composable(ScreenRoutes.Map.route) {
            MapScreen(
                onNavigateBack = { message ->
                    // Optionally, you could pass this message back to Favorites via SavedStateHandle,
                    // but popping the stack is all we need to return to the updated list!
                    navController.popBackStack()
                })
        }

// The Favorite Details Screen
        composable(
            route = ScreenRoutes.FavoriteDetails.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lon") { type = NavType.FloatType })) {
            // The ViewModel automatically catches the arguments from here via SavedStateHandle!
            FavoriteDetailsScreen(
                onBackClick = { navController.popBackStack() })
        }
    }
}