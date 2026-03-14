package com.example.wizzar.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryBlue,
    tertiary = PurpleAccent,
    background = BackgroundDark,
    surface = SkyBase,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onTertiary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
    error = AlertRed
)

// Optional: Light scheme mapped just in case, though the original design is strictly Dark.
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryBlue,
    tertiary = PurpleAccent,
    background = TextWhite,
    surface = Color(0xFFF3F4F6),
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onBackground = BackgroundDark,
    onSurface = BackgroundDark,
    error = AlertRed
)

@Composable
fun WizzarTheme(
    isDaytime: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // If dynamic color is used, we still respect the daytime state
            if (!isDaytime) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDaytime -> LightColorScheme
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}