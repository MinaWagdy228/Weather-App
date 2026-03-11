package com.example.wizzar.presentation.common

import java.util.Locale

// Extension function to convert digits to Arabic numerals if the locale is Arabic
fun String.toLocalizedNumbers(): String {
    val locale = Locale.getDefault()
    if (locale.language == "ar") {
        return this
            .replace("0", "٠")
            .replace("1", "١")
            .replace("2", "٢")
            .replace("3", "٣")
            .replace("4", "٤")
            .replace("5", "٥")
            .replace("6", "٦")
            .replace("7", "٧")
            .replace("8", "٨")
            .replace("9", "٩")
    }
    return this
}

fun Int.toLocalizedNumbers(): String {
    return this.toString().toLocalizedNumbers()
}

fun Double.toLocalizedNumbers(): String {
    return this.toString().toLocalizedNumbers()
}

fun Long.toLocalizedNumbers(): String {
    return this.toString().toLocalizedNumbers()
}

