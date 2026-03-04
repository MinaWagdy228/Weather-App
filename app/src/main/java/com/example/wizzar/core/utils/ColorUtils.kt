package com.example.wizzar.core.utils

import androidx.compose.ui.graphics.Color

/**
 * Color utility functions for Wizzar theme
 * Provides helper functions for color operations
 */

object ColorUtils {

    /**
     * Adjusts the brightness of a color
     * @param color The base color
     * @param factor Brightness factor (0.5f = 50% darker, 1.5f = 50% lighter)
     */
    fun adjustBrightness(color: Color, factor: Float): Color {
        val red = (color.red * 255 * factor).coerceIn(0f, 255f) / 255f
        val green = (color.green * 255 * factor).coerceIn(0f, 255f) / 255f
        val blue = (color.blue * 255 * factor).coerceIn(0f, 255f) / 255f
        return Color(red, green, blue, color.alpha)
    }

    /**
     * Creates a semi-transparent version of a color
     * @param color The base color
     * @param alpha Alpha value (0f = fully transparent, 1f = fully opaque)
     */
    fun withAlpha(color: Color, alpha: Float): Color {
        return color.copy(alpha = alpha.coerceIn(0f, 1f))
    }

    /**
     * Blends two colors together
     * @param color1 First color
     * @param color2 Second color
     * @param ratio Blend ratio (0f = 100% color1, 1f = 100% color2)
     */
    fun blendColors(color1: Color, color2: Color, ratio: Float): Color {
        val r = (color1.red * (1 - ratio) + color2.red * ratio).coerceIn(0f, 1f)
        val g = (color1.green * (1 - ratio) + color2.green * ratio).coerceIn(0f, 1f)
        val b = (color1.blue * (1 - ratio) + color2.blue * ratio).coerceIn(0f, 1f)
        val a = (color1.alpha * (1 - ratio) + color2.alpha * ratio).coerceIn(0f, 1f)
        return Color(r, g, b, a)
    }
}


