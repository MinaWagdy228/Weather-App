package com.example.wizzar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.example.wizzar.presentation.navigation.NavGraph
import com.example.wizzar.presentation.navigation.WizzarNavigationBar
import com.example.wizzar.ui.theme.BackgroundDark
import com.example.wizzar.ui.theme.WizzarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WizzarTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = { WizzarNavigationBar(navController = navController) },
                    containerColor = BackgroundDark
                ) { innerPadding ->
                    NavGraph(navController = navController, paddingValues = innerPadding)
                }
            }
        }
    }
}