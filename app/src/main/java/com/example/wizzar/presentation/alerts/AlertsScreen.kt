package com.example.wizzar.presentation.alerts

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizzar.domain.model.WeatherAlert
import com.example.wizzar.presentation.alerts.AlertsViewModel
import com.example.wizzar.presentation.common.glassmorphic
import com.example.wizzar.ui.theme.PrimaryBlue
import com.example.wizzar.ui.theme.TextWhite
import com.example.wizzar.ui.theme.Typography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    viewModel: AlertsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Bottom Sheet State
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // If denied, you could show a Snackbar explaining why it's needed
                if (!isGranted) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Notification permission is needed to receive weather alerts.",
                            actionLabel = "Grant",
                            duration = SnackbarDuration.Long
                        )
                    }
                }
        }
    )

    // 2. Request permission when the screen opens (for Android 13+)
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Weather Alerts", color = TextWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.AddAlert,
                            contentDescription = "Add Alert",
                            tint = TextWhite
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (uiState.alerts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("You haven't set any alarms yet.", color = TextWhite, style = Typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = uiState.alerts, key = { it.id }) { alert ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.removeAlert(alert.id)
                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Alarm for ${alert.cityName} deleted",
                                        actionLabel = "UNDO",
                                        duration = SnackbarDuration.Long // Approx 5 seconds
                                    )
                                    // REVERT BACK EXACT STATE IF UNDO CLICKED
                                    if (result == SnackbarResult.ActionPerformed) {
                                        // We re-insert the exact same alert with the same ID
                                        viewModel.toggleAlertActive(alert, alert.isActive)
                                    }
                                }
                                return@rememberSwipeToDismissBoxState true
                            }
                            return@rememberSwipeToDismissBoxState false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            val color by animateColorAsState(
                                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) Color.Red else Color.Transparent
                            )
                            Box(
                                modifier = Modifier.fillMaxSize().background(color, RoundedCornerShape(16.dp)).padding(end = 24.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                            }
                        },
                        content = {
                            AlertCityCard(
                                alert = alert,
                                onToggle = { isActive -> viewModel.toggleAlertActive(alert, isActive) }
                            )
                        }
                    )
                }
            }
        }

        // --- BOTTOM SHEET FOR ADDING ALERTS ---
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                AddAlertForm(
                    onSave = { startH, startM, endH, endM, isAlarm ->
                        val startTotal = (startH * 60) + startM
                        val endTotal = (endH * 60) + endM

                        if (endTotal <= startTotal) {
                            Toast.makeText(context, "End time must be after Start time!", Toast.LENGTH_SHORT).show()
                            return@AddAlertForm // Do not close sheet, revert to let them fix it
                        }

                        // 2. Save & trigger Toast via ViewModel callback
                        viewModel.addAlert(startH, startM, endH, endM, isAlarm) { toastMessage ->
                            Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()
                        }

                        // 3. Close the sheet
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet = false
                        }
                    },
                    onCancel = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AlertCityCard(alert: WeatherAlert, onToggle: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.glassmorphic(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(alert.cityName, color = TextWhite, style = Typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                // Displaying Start Time - End Time
                Text(
                    text = "${formatTime(alert.startTime)} - ${formatTime(alert.endTime)}",
                    color = Color.LightGray,
                    style = Typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (alert.isAlarmSound) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                        contentDescription = "Alert Type",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (alert.isAlarmSound) "Alarm" else "Notification", color = PrimaryBlue, style = Typography.labelMedium)
                }
            }

            Switch(
                checked = alert.isActive,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue, checkedTrackColor = PrimaryBlue.copy(alpha = 0.5f))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertForm(
    onSave: (Int, Int, Int, Int, Boolean) -> Unit,
    onCancel: () -> Unit
) {
    // Default times (e.g., 8:00 AM to 5:00 PM)
    var startHour by remember { mutableIntStateOf(8) }
    var startMinute by remember { mutableIntStateOf(0) }
    var endHour by remember { mutableIntStateOf(17) }
    var endMinute by remember { mutableIntStateOf(0) }

    // true = Alarm, false = Notification
    var isAlarm by remember { mutableStateOf(true) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text("Set Weather Alert", style = Typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(24.dp))

        // Start Duration Row
        Row(
            modifier = Modifier.fillMaxWidth().clickable { showStartTimePicker = true }.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Start Duration", style = Typography.bodyLarge)
            Text(formatTime((startHour * 60L) + startMinute), fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        // End Duration Row
        Row(
            modifier = Modifier.fillMaxWidth().clickable { showEndTimePicker = true }.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("End Duration", style = Typography.bodyLarge)
            Text(formatTime((endHour * 60L) + endMinute), fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        Spacer(modifier = Modifier.height(24.dp))

        // Notify me by Option
        Text("Notify me by", style = Typography.labelMedium, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = isAlarm, onClick = { isAlarm = true })
            Text("Alarm", modifier = Modifier.clickable { isAlarm = true })
            Spacer(modifier = Modifier.width(24.dp))
            RadioButton(selected = !isAlarm, onClick = { isAlarm = false })
            Text("Notification", modifier = Modifier.clickable { isAlarm = false })
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("CANCEL")
            }
            Button(onClick = { onSave(startHour, startMinute, endHour, endMinute, isAlarm) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                Text("SAVE")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    // TIME PICKER DIALOGS
    if (showStartTimePicker) {
        TimePickerDialog(
            initialHour = startHour, initialMinute = startMinute,
            onConfirm = { h, m -> startHour = h; startMinute = m; showStartTimePicker = false },
            onDismiss = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            initialHour = endHour, initialMinute = endMinute,
            onConfirm = { h, m -> endHour = h; endMinute = m; showEndTimePicker = false },
            onDismiss = { showEndTimePicker = false }
        )
    }
}

// --- HELPERS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialHour: Int, initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute, is24Hour = false)

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Select Time", style = Typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))

                TimePicker(state = timePickerState) // The beautiful M3 Clock interface!

                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) { Text("OK") }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
fun formatTime(minutesSinceMidnight: Long): String {
    val hour = (minutesSinceMidnight / 60).toInt()
    val minute = (minutesSinceMidnight % 60).toInt()
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = if (hour % 12 == 0) 12 else hour % 12
    return String.format("%02d:%02d %s", displayHour, minute, amPm)
}