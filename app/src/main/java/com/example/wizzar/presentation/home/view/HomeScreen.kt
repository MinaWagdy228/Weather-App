package com.example.wizzar.presentation.home.view

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizzar.domain.model.CurrentWeather
import com.example.wizzar.domain.model.DailyForecast
import com.example.wizzar.domain.model.HourlyForecast
import com.example.wizzar.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

// --- 1. STATEFUL COMPOSABLE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (isGranted) {
            viewModel.fetchWeatherForCurrentLocation(forceRefresh = true)
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

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
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
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
            is HomeUiState.Success -> {
                val pullToRefreshState = rememberPullToRefreshState()

                // The new, simplified PullToRefreshBox API!
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.fetchWeatherForCurrentLocation(forceRefresh = true) },
                    state = pullToRefreshState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    HomeScreenContent(
                        currentWeather = state.currentWeather,
                        hourlyForecast = state.hourlyForecast,
                        dailyForecast = state.dailyForecast
                    )
                }
            }
        }
    }
}

// ... Keep your HomeScreenContent and UI Components down below exactly as they were!
// --- 2. STATELESS COMPOSABLE (Pure UI) ---
@Composable
fun HomeScreenContent(
    currentWeather: CurrentWeather,
    hourlyForecast: List<HourlyForecast>,
    dailyForecast: List<DailyForecast>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 48.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { HeaderSection(currentWeather.city) }
        item { CurrentWeatherSection(currentWeather) }
        item { WeatherDetailsGrid(currentWeather) }
        item { HourlyForecastSection(hourlyForecast) }
        item { DailyForecastSection(dailyForecast) }
    }
}

// --- 3. UI COMPONENTS ---

@Composable
fun HeaderSection(location: String) {
    val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    val currentDate = dateFormat.format(Date())

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = LightBlueAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = location, color = TextWhite, style = Typography.headlineMedium)
            }
            Text(text = currentDate, color = TextGray, style = Typography.bodyMedium)
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PurpleAccent),
            contentAlignment = Alignment.Center
        ) {
            Text("A", color = TextWhite, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CurrentWeatherSection(currentWeather: CurrentWeather) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = mapOpenWeatherIcon(currentWeather.icon),
            contentDescription = currentWeather.description,
            tint = WarningYellow,
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = "${currentWeather.temperature.roundToInt()}°",
            color = TextWhite,
            style = Typography.displayLarge,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = currentWeather.description.replaceFirstChar { it.uppercase() } + " ✨",
            color = TextWhite,
            style = Typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "Feels like ${currentWeather.feelsLike.roundToInt()}° · High ${currentWeather.maxTemp.roundToInt()}° · Low ${currentWeather.minTemp.roundToInt()}°",
            color = TextGray,
            style = Typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun WeatherDetailsGrid(currentWeather: CurrentWeather) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            DetailCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.WaterDrop,
                iconTint = LightBlueAccent,
                value = "${currentWeather.humidity}%",
                label = "HUMIDITY"
            )
            DetailCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Air,
                iconTint = PurpleAccent,
                value = "${currentWeather.wind} m/s",
                label = "WIND SPEED"
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            DetailCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Thermostat,
                iconTint = AlertRed,
                value = "${currentWeather.pressure} hPa",
                label = "PRESSURE"
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun DetailCard(modifier: Modifier, icon: ImageVector, iconTint: Color, value: String, label: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = label, color = TextGray, style = Typography.labelMedium)
        }
    }
}

@Composable
fun HourlyForecastSection(hourly: List<HourlyForecast>) {
    val timeFormat = SimpleDateFormat("h a", Locale.getDefault())

    Column {
        SectionHeader("Today's Forecast", "See All")
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(hourly.take(8)) { item ->
                HourlyItemCard(item, timeFormat)
            }
        }
    }
}

@Composable
fun HourlyItemCard(item: HourlyForecast, timeFormat: SimpleDateFormat) {
    val timeString = timeFormat.format(Date(item.time * 1000L))

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = timeString, color = TextGray, style = Typography.labelMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Icon(
                imageVector = mapOpenWeatherIcon(item.icon),
                contentDescription = null,
                tint = WarningYellow,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "${item.temperature.roundToInt()}°", color = TextWhite, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DailyForecastSection(daily: List<DailyForecast>) {
    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    Column {
        SectionHeader("5-Day Forecast", "More")
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                daily.take(5).forEach { item ->
                    DailyItemRow(item, dayFormat)
                }
            }
        }
    }
}

@Composable
fun DailyItemRow(item: DailyForecast, dayFormat: SimpleDateFormat) {
    val dayString = dayFormat.format(Date(item.date * 1000L))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = dayString, color = TextWhite, style = Typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(
            imageVector = mapOpenWeatherIcon(item.icon),
            contentDescription = null,
            tint = WarningYellow,
            modifier = Modifier.weight(0.5f)
        )

        Box(modifier = Modifier.weight(1f).height(4.dp).background(BackgroundDark, CircleShape)) {
            Box(modifier = Modifier.fillMaxWidth(0.5f).height(4.dp).background(PurpleAccent, CircleShape))
        }

        Text(
            text = "${item.minTemp.roundToInt()}° / ${item.maxTemp.roundToInt()}°",
            color = TextWhite,
            style = Typography.bodyLarge,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun SectionHeader(title: String, actionText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = TextWhite, style = Typography.headlineMedium.copy(fontSize = 18.sp))
        Text(text = "$actionText →", color = PrimaryBlue, style = Typography.labelMedium)
    }
}

// --- 4. HELPER FUNCTION ---
fun mapOpenWeatherIcon(iconCode: String): ImageVector {
    return when (iconCode) {
        "01d" -> Icons.Outlined.WbSunny
        "01n" -> Icons.Outlined.NightsStay
        "02d", "02n", "03d", "03n", "04d", "04n" -> Icons.Outlined.CloudQueue
        "09d", "09n", "10d", "10n" -> Icons.Outlined.WaterDrop
        "11d", "11n" -> Icons.Outlined.Thunderstorm
        "13d", "13n" -> Icons.Outlined.AcUnit
        "50d", "50n" -> Icons.Outlined.Dehaze
        else -> Icons.Outlined.Cloud
    }
}