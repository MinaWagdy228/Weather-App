package com.example.wizzar.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.wizzar.ui.theme.* // Imports your centralized design tokens
import kotlin.random.Random

// --- 1. MODIFIERS ---
fun Modifier.glassmorphic(shape: Shape): Modifier = this
    .clip(shape)
    .background(
        Brush.linearGradient(
            colors = listOf(
                GlassBackgroundStart,
                GlassBackgroundEnd
            )
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                GlassBorderStart,
                GlassBorderEnd
            )
        ),
        shape = shape
    )

// --- 2. HELPERS ---
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

// --- 3. BACKGROUNDS ---
data class Star(val x: Float, val y: Float, val radius: Float, val alpha: Float)

@Composable
fun StarryBackground() {
    val stars = remember {
        List(150) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 2.5f + 0.5f,
                alpha = Random.nextFloat() * 0.6f + 0.2f
            )
        }
    }

    val skyGradient = Brush.verticalGradient(
        colors = listOf(
            SkyDarkTop,
            SkyMid,
            SkyBase
        )
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(skyGradient)
    ) {
        stars.forEach { star ->
            drawCircle(
                color = Color.White.copy(alpha = star.alpha),
                radius = star.radius,
                center = Offset(star.x * size.width, star.y * size.height)
            )
        }
    }
}

@Composable
fun SunnyBackground() {
    val skyGradient = Brush.verticalGradient(
        colors = listOf(
            SkyBlueTop,
            SkyBlueMid,
            SkyBlueBase
        )
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(skyGradient)
    ) {
        // Position the sun in the top right quadrant
        val sunCenter = Offset(size.width * 0.85f, size.height * 0.15f)
        val sunRadius = 120f

        // 1. Draw the soft, radiating glow (large and semi-transparent)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(SunGlow.copy(alpha = 0.6f), Color.Transparent),
                center = sunCenter,
                radius = sunRadius * 3.5f
            ),
            radius = sunRadius * 3.5f,
            center = sunCenter
        )

        // 2. Draw the inner corona (slightly denser glow)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(SunGlow.copy(alpha = 0.8f), Color.Transparent),
                center = sunCenter,
                radius = sunRadius * 1.5f
            ),
            radius = sunRadius * 1.5f,
            center = sunCenter
        )

    }
}