package com.example.wizzar.presentation.alerts.weatherAlarm

import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wizzar.presentation.common.StarryBackground
import com.example.wizzar.presentation.common.glassmorphic
import com.example.wizzar.ui.theme.PrimaryBlue
import com.example.wizzar.ui.theme.TextWhite
import com.example.wizzar.ui.theme.Typography
import com.example.wizzar.ui.theme.WizzarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherAlarmActivity : ComponentActivity() {

    private val viewModel: WeatherAlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )

        val alertId = intent.getStringExtra("ALERT_ID") ?: ""
        val cityName = intent.getStringExtra("CITY_NAME") ?: "Unknown"
        val description = intent.getStringExtra("DESCRIPTION") ?: "Severe conditions"

        setContent {
            WizzarTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    StarryBackground()
                    AlarmScreenContent(
                        cityName = cityName,
                        description = description,
                        onSnooze = { handleAction(alertId, isSnooze = true) },
                        onCancel = { handleAction(alertId, isSnooze = false) }
                    )
                }
            }
        }
    }

    private fun handleAction(alertId: String, isSnooze: Boolean) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(alertId.hashCode())

        if (isSnooze) {
            viewModel.snooze(alertId) { finish() }
        } else {
            viewModel.dismiss(alertId) { finish() }
        }
    }
}

@Composable
fun AlarmScreenContent(cityName: String, description: String, onSnooze: () -> Unit, onCancel: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .glassmorphic(RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Alert",
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "WEATHER ALARM",
                    style = Typography.headlineMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = cityName,
                    style = Typography.titleLarge,
                    color = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = description,
                    style = Typography.bodyLarge,
                    color = TextWhite.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = onSnooze,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SNOOZE (10 MIN)", style = Typography.labelLarge)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TextWhite.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("DISMISS", style = Typography.labelLarge)
                }
            }
        }
    }
}

