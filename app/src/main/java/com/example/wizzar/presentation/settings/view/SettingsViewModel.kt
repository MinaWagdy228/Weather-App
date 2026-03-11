package com.example.wizzar.presentation.settings.view

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.data.dataSource.local.datastore.AppLanguage
import com.example.wizzar.data.dataSource.local.datastore.LocationMode
import com.example.wizzar.data.dataSource.local.datastore.TempUnit
import com.example.wizzar.data.dataSource.local.datastore.UserSettings
import com.example.wizzar.data.dataSource.local.datastore.WindUnit
import com.example.wizzar.domain.usecase.ManageSettingsUseCase // <-- INJECTING THE USE CASE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val manageSettingsUseCase: ManageSettingsUseCase
) : ViewModel() {

    val settingsState: StateFlow<UserSettings> = manageSettingsUseCase.observeSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings(
                language = AppLanguage.DEFAULT,
                tempUnit = TempUnit.CELSIUS,
                windUnit = WindUnit.METER_SEC,
                locationMode = LocationMode.GPS,
                mapLat = null,
                mapLon = null
            )
        )

    private val _uiEvent = MutableSharedFlow<SettingsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun updateLanguage(language: AppLanguage) {
        viewModelScope.launch {
            manageSettingsUseCase.updateLanguage(language)
            // Tell Android to physically switch the app's resource locale
            val languageCode = if (language == AppLanguage.ARABIC) "ar" else "en"
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(languageCode)
            )
        }
    }

    fun updateTempUnit(unit: TempUnit) {
        viewModelScope.launch {
            manageSettingsUseCase.updateTempUnit(unit)
        }
    }

    fun updateWindUnit(unit: WindUnit) {
        viewModelScope.launch {
            manageSettingsUseCase.updateWindUnit(unit)
        }
    }

    fun updateLocationMode(mode: LocationMode) {
        viewModelScope.launch {
            if (mode == LocationMode.MAP) {
                _uiEvent.emit(SettingsUiEvent.NavigateToMap)
            } else {
                manageSettingsUseCase.updateLocationMode(mode)
            }
        }
    }
}

sealed interface SettingsUiEvent {
    object NavigateToMap : SettingsUiEvent
}