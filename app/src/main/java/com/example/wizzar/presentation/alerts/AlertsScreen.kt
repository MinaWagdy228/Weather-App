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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizzar.domain.model.WeatherAlert
import com.example.wizzar.presentation.common.glassmorphic
import com.example.wizzar.ui.theme.PrimaryBlue
import com.example.wizzar.ui.theme.TextWhite
import com.example.wizzar.ui.theme.Typography
import kotlinx.coroutines.launch
import com.example.wizzar.R

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
    var alertToEdit by remember { mutableStateOf<WeatherAlert?>(null) } // Track which alert is being edited

    val permissionNotificationNeeded = stringResource(R.string.permission_notification_needed)
    val grant = stringResource(R.string.grant)
    val alarmDeletedTemplate = stringResource(R.string.alarm_deleted)
    val undo = stringResource(R.string.undo)
    val endTimeError = stringResource(R.string.end_time_error)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
                if (!isGranted) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = permissionNotificationNeeded,
                            actionLabel = grant,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
        }
    )

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
                title = { Text(stringResource(R.string.weather_alerts_title), color = TextWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = {
                        alertToEdit = null // Reset for adding new alert
                        showBottomSheet = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.AddAlert,
                            contentDescription = stringResource(R.string.add_alert_desc),
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
                Text(stringResource(R.string.no_alarms_set), color = TextWhite, style = Typography.bodyLarge)
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
                                        message = String.format(alarmDeletedTemplate, alert.cityName),
                                        actionLabel = undo,
                                        duration = SnackbarDuration.Long
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.undoRemoveAlert(alert)                                    }
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
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_desc), tint = Color.White)
                            }
                        },
                        content = {
                            AlertCityCard(
                                alert = alert,
                                onToggle = { isActive -> viewModel.toggleAlertActive(alert, isActive) },
                                onClick = {
                                    alertToEdit = alert
                                    showBottomSheet = true
                                }
                            )
                        }
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                // Determine initial values based on whether we are editing or adding
                val initialStartHour = if (alertToEdit != null) (alertToEdit!!.startTime / 60).toInt() else 8
                val initialStartMinute = if (alertToEdit != null) (alertToEdit!!.startTime % 60).toInt() else 0
                val initialEndHour = if (alertToEdit != null) (alertToEdit!!.endTime / 60).toInt() else 17
                val initialEndMinute = if (alertToEdit != null) (alertToEdit!!.endTime % 60).toInt() else 0
                val initialIsAlarm = alertToEdit?.isAlarmSound ?: true

                AddAlertForm(
                    initialStartHour = initialStartHour,
                    initialStartMinute = initialStartMinute,
                    initialEndHour = initialEndHour,
                    initialEndMinute = initialEndMinute,
                    initialIsAlarm = initialIsAlarm,
                    onSave = { startH, startM, endH, endM, isAlarm ->
                        val startTotal = (startH * 60) + startM
                        val endTotal = (endH * 60) + endM

                        if (endTotal <= startTotal) {
                            Toast.makeText(context, endTimeError, Toast.LENGTH_SHORT).show()
                            return@AddAlertForm // Do not close sheet, revert to let them fix it
                        }

                        // 2. Save & trigger Toast via ViewModel callback
                        if (alertToEdit == null) {
                            viewModel.addAlert(startH, startM, endH, endM, isAlarm) { alertMessage ->
                                val msg = when (alertMessage) {
                                    is AlertMessage.StringValue -> alertMessage.value
                                    is AlertMessage.StringResource -> context.getString(alertMessage.resId, *alertMessage.args)
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        } else {
                            viewModel.updateAlert(alertToEdit!!, startH, startM, endH, endM, isAlarm) { alertMessage ->
                                val msg = when (alertMessage) {
                                    is AlertMessage.StringValue -> alertMessage.value
                                    is AlertMessage.StringResource -> context.getString(alertMessage.resId, *alertMessage.args)
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
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
fun AlertCityCard(alert: WeatherAlert, onToggle: (Boolean) -> Unit, onClick: () -> Unit) {
    val am = stringResource(R.string.time_am)
    val pm = stringResource(R.string.time_pm)

    Card(
        modifier = Modifier.glassmorphic(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick), // Make card clickable
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
                    text = "${formatTime(alert.startTime, am, pm)} - ${formatTime(alert.endTime, am, pm)}",
                    color = Color.LightGray,
                    style = Typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val alertType = if (alert.isAlarmSound) stringResource(R.string.alarm) else stringResource(R.string.notification)
                    Icon(
                        imageVector = if (alert.isAlarmSound) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                        contentDescription = alertType,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = alertType, color = PrimaryBlue, style = Typography.labelMedium)
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
    initialStartHour: Int = 8,
    initialStartMinute: Int = 0,
    initialEndHour: Int = 17,
    initialEndMinute: Int = 0,
    initialIsAlarm: Boolean = true,
    onSave: (Int, Int, Int, Int, Boolean) -> Unit,
    onCancel: () -> Unit
) {
    // Default times (e.g., 8:00 AM to 5:00 PM)
    var startHour by remember { mutableIntStateOf(initialStartHour) }
    var startMinute by remember { mutableIntStateOf(initialStartMinute) }
    var endHour by remember { mutableIntStateOf(initialEndHour) }
    var endMinute by remember { mutableIntStateOf(initialEndMinute) }

    // true = Alarm, false = Notification
    var isAlarm by remember { mutableStateOf(initialIsAlarm) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val startDuration = stringResource(R.string.start_duration)
    val endDuration = stringResource(R.string.end_duration)
    val labelNotifyBy = stringResource(R.string.notify_me_by)
    val labelAlarm = stringResource(R.string.alarm)
    val labelNotification = stringResource(R.string.notification)
    val labelCancel = stringResource(R.string.cancel)
    val labelSave = stringResource(R.string.save)

    val am = stringResource(R.string.time_am)
    val pm = stringResource(R.string.time_pm)

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(stringResource(R.string.weather_alerts_title), style = Typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(24.dp))

        // Start Duration Row
        Row(
            modifier = Modifier.fillMaxWidth().clickable { showStartTimePicker = true }.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(startDuration, style = Typography.bodyLarge)
            Text(formatTime((startHour * 60L) + startMinute, am, pm), fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        // End Duration Row
        Row(
            modifier = Modifier.fillMaxWidth().clickable { showEndTimePicker = true }.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(endDuration, style = Typography.bodyLarge)
            Text(formatTime((endHour * 60L) + endMinute, am, pm), fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        Spacer(modifier = Modifier.height(24.dp))

        // Notify me by Option
        Text(labelNotifyBy, style = Typography.labelMedium, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = isAlarm, onClick = { isAlarm = true })
            Text(labelAlarm, modifier = Modifier.clickable { isAlarm = true })
            Spacer(modifier = Modifier.width(24.dp))
            RadioButton(selected = !isAlarm, onClick = { isAlarm = false })
            Text(labelNotification, modifier = Modifier.clickable { isAlarm = false })
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text(labelCancel)
            }
            Button(onClick = { onSave(startHour, startMinute, endHour, endMinute, isAlarm) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                Text(labelSave)
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
                Text(stringResource(R.string.select_time), style = Typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))

                TimePicker(state = timePickerState) // The beautiful M3 Clock interface!

                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                    TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) { Text("OK") }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
fun formatTime(minutesSinceMidnight: Long, am: String = "AM", pm: String = "PM"): String {
    val hour = (minutesSinceMidnight / 60).toInt()
    val minute = (minutesSinceMidnight % 60).toInt()
    val amPm = if (hour < 12) am else pm
    val displayHour = if (hour % 12 == 0) 12 else hour % 12
    return String.format("%02d:%02d %s", displayHour, minute, amPm)
}