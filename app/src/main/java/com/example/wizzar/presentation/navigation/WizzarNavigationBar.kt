package com.example.wizzar.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.wizzar.ui.theme.PrimaryBlue
import com.example.wizzar.ui.theme.SurfaceDark
import com.example.wizzar.ui.theme.TextGray
import com.example.wizzar.ui.theme.TextWhite

@Composable
fun WizzarNavigationBar(navController: NavController) {
    val items = listOf(
        ScreenRoutes.Home,
        ScreenRoutes.Favorites,
        ScreenRoutes.Alerts,
        ScreenRoutes.Settings
    )

    NavigationBar(
        containerColor = SurfaceDark,
        contentColor = TextWhite
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { Text(text = screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to avoid building up a large stack
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray,
                    indicatorColor = SurfaceDark // Keeps the background unified when selected
                )
            )
        }
    }
}