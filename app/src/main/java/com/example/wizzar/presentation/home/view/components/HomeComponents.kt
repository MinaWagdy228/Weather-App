package com.example.wizzar.presentation.home.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wizzar.R
import com.example.wizzar.domain.model.CurrentWeather
import com.example.wizzar.domain.model.DailyForecast
import com.example.wizzar.domain.model.HourlyForecast
import com.example.wizzar.presentation.common.glassmorphic
import com.example.wizzar.presentation.common.mapOpenWeatherIcon
import com.example.wizzar.presentation.common.toLocalizedNumbers
import com.example.wizzar.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

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
    }
}

@Composable
fun CurrentWeatherSection(currentWeather: CurrentWeather, tempUnit: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphic(RoundedCornerShape(24.dp))
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = mapOpenWeatherIcon(currentWeather.icon),
            contentDescription = currentWeather.description,
            tint = WarningYellow,
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = "${currentWeather.temperature.roundToInt().toLocalizedNumbers()}$tempUnit",
            color = TextWhite,
            style = Typography.displayLarge,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = currentWeather.description.replaceFirstChar { it.uppercase() },
            color = TextWhite,
            style = Typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = stringResource(
                R.string.feels_like_high_low,
                "${currentWeather.feelsLike.roundToInt().toLocalizedNumbers()}$tempUnit",
                "${currentWeather.maxTemp.roundToInt().toLocalizedNumbers()}$tempUnit",
                "${currentWeather.minTemp.roundToInt().toLocalizedNumbers()}$tempUnit"
            ),
            color = TextGray,
            style = Typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun WeatherDetailsGrid(currentWeather: CurrentWeather, windUnit: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max)
        ) {
            DetailCard(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                icon = Icons.Outlined.WaterDrop,
                iconTint = LightBlueAccent,
                value = "${currentWeather.humidity.toLocalizedNumbers()}%",
                label = stringResource(R.string.humidity)
            )
            DetailCard(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                icon = Icons.Outlined.Air,
                iconTint = PurpleAccent,
                value = "${currentWeather.wind.toLocalizedNumbers()} $windUnit",
                label = stringResource(R.string.wind_speed)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max)
        ) {
            DetailCard(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                icon = Icons.Outlined.Thermostat,
                iconTint = AlertRed,
                value = "${currentWeather.pressure.toLocalizedNumbers()} hPa",
                label = stringResource(R.string.pressure)
            )
            SunriseSunsetCard(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                sunrise = currentWeather.sunrise,
                sunset = currentWeather.sunset,
                sunriseLabel = stringResource(R.string.sunrise),
                sunsetLabel = stringResource(R.string.sunset)
            )
        }
    }
}

@Composable
fun DetailCard(modifier: Modifier, icon: ImageVector, iconTint: Color, value: String, label: String) {
    Box(
        modifier = modifier.glassmorphic(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = label, color = TextGray, style = Typography.labelMedium)
        }
    }
}

@Composable
fun SunriseSunsetCard(modifier: Modifier, sunrise: Long, sunset: Long, sunriseLabel: String, sunsetLabel: String) {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val sunriseTime = timeFormat.format(Date(sunrise * 1000L)).toLocalizedNumbers()
    val sunsetTime = timeFormat.format(Date(sunset * 1000L)).toLocalizedNumbers()

    Box(modifier = modifier.glassmorphic(RoundedCornerShape(16.dp))) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Outlined.WbSunny, contentDescription = "Sunrise", tint = WarningYellow)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = sunriseTime, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = sunriseLabel, color = TextGray, style = Typography.labelMedium)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Outlined.NightsStay, contentDescription = "Sunset", tint = PurpleAccent)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = sunsetTime, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = sunsetLabel, color = TextGray, style = Typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun HourlyForecastSection(hourly: List<HourlyForecast>, tempUnit: String) {
    val timeFormat = SimpleDateFormat("h a", Locale.getDefault())

    Column {
        SectionHeader(stringResource(R.string.todays_forecast))
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(hourly.take(8)) { item ->
                HourlyItemCard(item, timeFormat, tempUnit)
            }
        }
    }
}

@Composable
fun HourlyItemCard(item: HourlyForecast, timeFormat: SimpleDateFormat, tempUnit: String) {
    val timeString = timeFormat.format(Date(item.time * 1000L)).toLocalizedNumbers()

    Column(
        modifier = Modifier
            .glassmorphic(RoundedCornerShape(20.dp))
            .padding(vertical = 16.dp, horizontal = 20.dp),
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
        Text(text = "${item.temperature.roundToInt().toLocalizedNumbers()}$tempUnit", color = TextWhite, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DailyForecastSection(daily: List<DailyForecast>, tempUnit: String) {
    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    Column {
        SectionHeader(stringResource(R.string.five_day_forecast))
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .glassmorphic(RoundedCornerShape(24.dp))
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                daily.take(5).forEach { item ->
                    DailyItemRow(item, dayFormat, tempUnit)
                }
            }
        }
    }
}

@Composable
fun DailyItemRow(item: DailyForecast, dayFormat: SimpleDateFormat, tempUnit: String) {
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

        Text(
            text = "${item.minTemp.roundToInt().toLocalizedNumbers()}$tempUnit / ${item.maxTemp.roundToInt().toLocalizedNumbers()}$tempUnit",
            color = TextWhite,
            style = Typography.bodyLarge,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = TextWhite, style = Typography.headlineMedium.copy(fontSize = 18.sp))
    }
}