package com.example.wizzar.presentation.home.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizzar.domain.usercase.WeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherUseCase: WeatherUseCase
) : ViewModel() {

    val weather = weatherUseCase.observeWeather().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )
}