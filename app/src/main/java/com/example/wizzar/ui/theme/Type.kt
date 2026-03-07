package com.example.wizzar.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// TODO: To apply the exact web look, uncomment these once you add the fonts to res/font folder.
// val SyneFontFamily = FontFamily(
//     Font(R.font.syne_regular, FontWeight.Normal),
//     Font(R.font.syne_semibold, FontWeight.SemiBold),
//     Font(R.font.syne_bold, FontWeight.Bold),
//     Font(R.font.syne_extrabold, FontWeight.ExtraBold)
// )
//
// val DmSansFontFamily = FontFamily(
//     Font(R.font.dmsans_regular, FontWeight.Normal),
//     Font(R.font.dmsans_medium, FontWeight.Medium),
//     Font(R.font.dmsans_semibold, FontWeight.SemiBold)
// )

val Typography = Typography(
    // Used for massive temp displays (e.g., 80px Syne)
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default, // Replace with SyneFontFamily
        fontWeight = FontWeight.ExtraBold,
        fontSize = 80.sp,
        lineHeight = 80.sp,
        letterSpacing = (-3).sp
    ),
    // Used for screen titles
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default, // Replace with SyneFontFamily
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    // Used for standard UI text
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default, // Replace with DmSansFontFamily
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Used for smaller UI details / descriptions
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default, // Replace with DmSansFontFamily
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = TextGray
    ),
    // Used for buttons and small labels
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default, // Replace with DmSansFontFamily
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)