package com.example.wizzar.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.wizzar.R
import com.example.wizzar.ui.theme.PrimaryBlue
import com.example.wizzar.ui.theme.SkyBase
import com.example.wizzar.ui.theme.SkyBlueBase
import com.example.wizzar.ui.theme.TextGray
import com.example.wizzar.ui.theme.TextWhite

@Composable
fun WizzarNavigationBar(navController: NavController, mainViewModel: MainViewModel) {
    val items = listOf(
        ScreenRoutes.Home,
        ScreenRoutes.Favorites,
        ScreenRoutes.Alerts,
        ScreenRoutes.Settings
    )

    // 1. Observe the lightweight boolean directly
    val isDaytime by mainViewModel.isDaytime.collectAsStateWithLifecycle()

    // 2. Select colors dynamically
    val barColor = if (isDaytime) SkyBlueBase else SkyBase
    val unselectedContentColor = if (isDaytime) Color.Gray else TextGray
    val contentColor = if (isDaytime) Color.Black else TextWhite

    NavigationBar(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            .clip(RoundedCornerShape(32.dp)),
        containerColor = barColor,
        contentColor = contentColor,
        tonalElevation = 8.dp // Adds a subtle drop shadow to make it "float"
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            val localizedTitle = when (screen) {
                ScreenRoutes.Home -> stringResource(R.string.nav_home)
                ScreenRoutes.Favorites -> stringResource(R.string.nav_favorites)
                ScreenRoutes.Alerts -> stringResource(R.string.nav_alerts)
                ScreenRoutes.Settings -> stringResource(R.string.nav_settings)
                else -> screen.title
            }

            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = localizedTitle) },
                label = { Text(text = localizedTitle) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
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
                    unselectedIconColor = unselectedContentColor,
                    unselectedTextColor = unselectedContentColor,
                    indicatorColor = barColor
                )
            )
        }
    }
}