package com.example.wizzar.presentation.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.domain.usecase.ManageAlertsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherAlarmViewModel @Inject constructor(
    private val manageAlertsUseCase: ManageAlertsUseCase
) : ViewModel() {

    fun snooze(alertId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            manageAlertsUseCase.snoozeAlert(alertId)
            onComplete()
        }
    }

    fun dismiss(alertId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            manageAlertsUseCase.dismissAlertForToday(alertId)
            onComplete()
        }
    }
}