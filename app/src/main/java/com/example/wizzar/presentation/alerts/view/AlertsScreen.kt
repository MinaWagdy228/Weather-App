package com.example.wizzar.presentation.alerts.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wizzar.R
import com.example.wizzar.ui.theme.BackgroundDark
import com.example.wizzar.ui.theme.TextWhite
import com.example.wizzar.ui.theme.Typography

@Composable
fun AlertsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.alerts_screen_title), color = TextWhite, style = Typography.headlineMedium)
    }
}