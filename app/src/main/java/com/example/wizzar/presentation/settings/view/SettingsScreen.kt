package com.example.wizzar.presentation.settings.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.wizzar.ui.theme.BackgroundDark
import com.example.wizzar.ui.theme.TextWhite
import com.example.wizzar.ui.theme.Typography

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Settings Screen", color = TextWhite, style = Typography.headlineMedium)
    }
}