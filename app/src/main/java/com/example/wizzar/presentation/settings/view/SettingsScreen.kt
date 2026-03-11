package com.example.wizzar.presentation.settings.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizzar.R
import com.example.wizzar.data.dataSource.local.datastore.AppLanguage
import com.example.wizzar.data.dataSource.local.datastore.LocationMode
import com.example.wizzar.data.dataSource.local.datastore.TempUnit
import com.example.wizzar.data.dataSource.local.datastore.WindUnit
import com.example.wizzar.presentation.common.glassmorphic
import com.example.wizzar.ui.theme.PrimaryBlue
import com.example.wizzar.ui.theme.TextGray
import com.example.wizzar.ui.theme.TextWhite
import com.example.wizzar.ui.theme.Typography
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit
) {
    val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SettingsUiEvent.NavigateToMap -> onNavigateToMap()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            color = TextWhite,
            style = Typography.headlineMedium
        )

        SettingsCard(title = stringResource(R.string.location_source)) {
            SegmentedControl(
                items = listOf(stringResource(R.string.gps), stringResource(R.string.map_pin)),
                selectedIndex = if (settingsState.locationMode == LocationMode.GPS) 0 else 1,
                onItemSelected = { index ->
                    viewModel.updateLocationMode(if (index == 0) LocationMode.GPS else LocationMode.MAP)
                }
            )
        }

        SettingsCard(title = stringResource(R.string.temperature_unit)) {
            SegmentedControl(
                items = listOf(stringResource(R.string.celsius), stringResource(R.string.fahrenheit), stringResource(R.string.kelvin)),
                selectedIndex = when (settingsState.tempUnit) {
                    TempUnit.CELSIUS -> 0
                    TempUnit.FAHRENHEIT -> 1
                    TempUnit.KELVIN -> 2
                },
                onItemSelected = { index ->
                    val unit = when (index) {
                        0 -> TempUnit.CELSIUS
                        1 -> TempUnit.FAHRENHEIT
                        else -> TempUnit.KELVIN
                    }
                    viewModel.updateTempUnit(unit)
                }
            )
        }

        SettingsCard(title = stringResource(R.string.wind_speed_unit)) {
            SegmentedControl(
                items = listOf(stringResource(R.string.meters_per_sec), stringResource(R.string.miles_per_hour)),
                selectedIndex = if (settingsState.windUnit == WindUnit.METER_SEC) 0 else 1,
                onItemSelected = { index ->
                    viewModel.updateWindUnit(if (index == 0) WindUnit.METER_SEC else WindUnit.MILE_HOUR)
                }
            )
        }

        SettingsCard(title = stringResource(R.string.api_language)) {
            SegmentedControl(
                items = listOf(stringResource(R.string.english), stringResource(R.string.arabic)),
                selectedIndex = if (settingsState.language == AppLanguage.ARABIC) 1 else 0,
                onItemSelected = { index ->
                    viewModel.updateLanguage(if (index == 0) AppLanguage.ENGLISH else AppLanguage.ARABIC)
                }
            )
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphic(RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = TextWhite
        )
        content()
    }
}

@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.2f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEachIndexed { index, text ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) PrimaryBlue else Color.Transparent)
                    .clickable { onItemSelected(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = if (isSelected) TextWhite else TextGray,
                    style = Typography.labelMedium
                )
            }
        }
    }
}