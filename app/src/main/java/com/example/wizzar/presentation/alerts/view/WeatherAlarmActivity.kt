package com.example.wizzar.presentation.alerts.view

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wizzar.ui.theme.BackgroundDark
import com.example.wizzar.ui.theme.PrimaryBlue
import com.example.wizzar.ui.theme.Typography
import com.example.wizzar.ui.theme.WizzarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherAlarmActivity : ComponentActivity() {

    // 👈 STRICT MVVM: We only inject the ViewModel!
    private val viewModel: WeatherAlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Wake up the screen
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
                Surface(modifier = Modifier.fillMaxSize(), color = BackgroundDark) {
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
        // 1. Clear the notification/audio
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(alertId.hashCode())

        // 2. Delegate to ViewModel, and close the Activity ONLY when DB update is complete
        if (isSnooze) {
            viewModel.snooze(alertId) { finish() }
        } else {
            viewModel.dismiss(alertId) { finish() }
        }
    }
}
@Composable
fun AlarmScreenContent(cityName: String, description: String, onSnooze: () -> Unit, onCancel: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WEATHER ALARM",
            style = Typography.displayLarge.copy(fontSize = 32.sp),
            color = Color.Red,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = cityName, style = Typography.headlineMedium, color = Color.White)
        Text(text = description, style = Typography.bodyLarge, color = Color.LightGray)

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = onSnooze,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("SNOOZE (10 MIN)", style = Typography.labelMedium.copy(fontSize = 18.sp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("DISMISS", color = Color.White, style = Typography.labelMedium.copy(fontSize = 18.sp))
        }
    }
}