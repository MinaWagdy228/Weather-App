package com.example.wizzar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.wizzar.presentation.common.StarryBackground
import com.example.wizzar.presentation.common.SunnyBackground
import com.example.wizzar.presentation.main.MainViewModel
import com.example.wizzar.presentation.navigation.NavGraph
import com.example.wizzar.presentation.navigation.WizzarNavigationBar
import com.example.wizzar.ui.theme.WizzarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WizzarTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = hiltViewModel()

                // Observe daytime state at the absolute root of the app
                val isDaytime by mainViewModel.isDaytime.collectAsStateWithLifecycle()

                Box(modifier = Modifier.fillMaxSize()) {
                    if (!isDaytime) {
                        SunnyBackground()
                    } else {
                        StarryBackground()
                    }

                    Scaffold(
                        bottomBar = { WizzarNavigationBar(navController = navController, mainViewModel = mainViewModel) },
                        containerColor = Color.Transparent
                    ) { innerPadding ->
                        NavGraph(navController = navController, paddingValues = innerPadding)
                    }
                }
            }
        }
    }
}