package com.example.wizzar.presentation.home.view

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizzar.data.dataSource.local.datastore.TempUnit
import com.example.wizzar.data.dataSource.local.datastore.WindUnit
import com.example.wizzar.domain.model.CurrentWeather
import com.example.wizzar.domain.model.DailyForecast
import com.example.wizzar.domain.model.HourlyForecast
import com.example.wizzar.presentation.home.view.components.*
import com.example.wizzar.ui.theme.AlertRed
import com.example.wizzar.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (isGranted) {
            viewModel.fetchWeatherForCurrentLocation(forceRefresh = true)
        } else {
            Toast.makeText(
                context,
                "Location permission is needed for local weather.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        when (val state = uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator(
                    color = PrimaryBlue,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is HomeUiState.Error -> {
                Text(
                    text = state.message,
                    color = AlertRed,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }

            is HomeUiState.Success -> {
                val pullToRefreshState = rememberPullToRefreshState()

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.fetchWeatherForCurrentLocation(forceRefresh = true) },
                    state = pullToRefreshState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    HomeScreenContent(
                        currentWeather = state.currentWeather,
                        hourlyForecast = state.hourlyForecast,
                        dailyForecast = state.dailyForecast,
                        tempUnit = state.tempUnit,
                        windUnit = state.windUnit
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    currentWeather: CurrentWeather,
    hourlyForecast: List<HourlyForecast>,
    dailyForecast: List<DailyForecast>,
    tempUnit: TempUnit,
    windUnit: WindUnit
) {
    val tempUnitSymbol = when(tempUnit) {
        TempUnit.CELSIUS -> "°C"
        TempUnit.FAHRENHEIT -> "°F"
        TempUnit.KELVIN -> "°K"
    }
    val windUnitSymbol = when(windUnit) {
        WindUnit.METER_SEC -> "m/s"
        WindUnit.MILE_HOUR -> "mph"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 48.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { HeaderSection(currentWeather.city) }
        item { CurrentWeatherSection(currentWeather, tempUnitSymbol) }
        item { WeatherDetailsGrid(currentWeather, windUnitSymbol) }
        item { HourlyForecastSection(hourlyForecast, tempUnitSymbol) }
        item { DailyForecastSection(dailyForecast, tempUnitSymbol) }
    }
}