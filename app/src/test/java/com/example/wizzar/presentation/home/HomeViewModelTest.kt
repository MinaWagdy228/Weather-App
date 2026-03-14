package com.example.wizzar.presentation.home

import app.cash.turbine.test
import com.example.wizzar.data.dataSource.local.datastore.*
import com.example.wizzar.domain.model.CurrentWeather
import com.example.wizzar.domain.util.DomainError
import com.example.wizzar.domain.model.Location
import com.example.wizzar.domain.util.Result
import com.example.wizzar.domain.model.WeatherData
import com.example.wizzar.domain.usecase.GetActiveLocationUseCase
import com.example.wizzar.domain.usecase.GetWeatherUseCase
import com.example.wizzar.domain.usecase.ManageSettingsUseCase
import com.example.wizzar.utils.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mocked Dependencies
    private val getWeatherUseCase: GetWeatherUseCase = mockk()
    private val getActiveLocationUseCase: GetActiveLocationUseCase = mockk()
    private val manageSettingsUseCase: ManageSettingsUseCase = mockk()

    // Dummy Data
    private val defaultSettings = UserSettings(
        language = AppLanguage.ENGLISH, tempUnit = TempUnit.CELSIUS, windUnit = WindUnit.METER_SEC,
        locationMode = LocationMode.GPS, mapLat = null, mapLon = null
    )
    private val validLocation = Location(30.044, 31.235)
    private val dummyWeatherData = WeatherData(
        currentWeather = mockk<CurrentWeather>(relaxed = true),
        hourlyForecast = emptyList(),
        dailyForecast = emptyList()
    )

    @Test
    fun init_validLocationAndData_emitsSuccessState() = runTest {
        // Given ViewModel initializes, it should fetch weather for current location and emit Success state if all goes well.
        coEvery { getActiveLocationUseCase.execute() } returns validLocation
        coEvery { manageSettingsUseCase.observeSettings() } returns flowOf(defaultSettings)
        coEvery { getWeatherUseCase.refreshWeather(any(), any(), any(), any()) } returns Result.Success(dummyWeatherData)
        coEvery { getWeatherUseCase.observeWeather(any(), any()) } returns flowOf(dummyWeatherData)

        // When ViewModel is instantiated
        val viewModel = HomeViewModel(getWeatherUseCase, getActiveLocationUseCase, manageSettingsUseCase)

        // Then it should emit Loading followed by Success with correct data
        viewModel.uiState.test {
            val firstState = awaitItem()
            if (firstState is HomeUiState.Loading) {
                assertThat(awaitItem()).isInstanceOf(HomeUiState.Success::class.java)
            } else {
                assertThat(firstState).isInstanceOf(HomeUiState.Success::class.java)
            }
            // Verify correct coordinates were passed
            coVerify { getWeatherUseCase.refreshWeather(30.044, 31.235, "en", false) }
        }
    }

    @Test
    fun init_nullLocation_emitsErrorState() = runTest {
        // Given GPS fails to provide location (returns null)
        coEvery { getActiveLocationUseCase.execute() } returns null
        coEvery { manageSettingsUseCase.observeSettings() } returns flowOf(defaultSettings)

        // When ViewModel is instantiated
        val viewModel = HomeViewModel(getWeatherUseCase, getActiveLocationUseCase, manageSettingsUseCase)

        // Then it should emit Loading followed by Error with appropriate message
        viewModel.uiState.test {
            // Safely grab the first item. It will likely already be Error, but might be Loading.
            val firstState = awaitItem()
            val errorState = if (firstState is HomeUiState.Loading) {
                awaitItem() as HomeUiState.Error
            } else {
                firstState as HomeUiState.Error
            }

            assertThat(errorState.message).contains("Ensure GPS is enabled")

            // Prove network was never called
            coVerify(exactly = 0) { getWeatherUseCase.refreshWeather(any(), any(), any(), any()) }
        }
    }

    @Test
    fun fetchWeatherForCurrentLocation_networkError_emitsToUiEvent() = runTest {
        // Given ViewModel has valid location and settings, but network fails when refreshing weather
        coEvery { getActiveLocationUseCase.execute() } returns validLocation
        coEvery { manageSettingsUseCase.observeSettings() } returns flowOf(defaultSettings)
        coEvery { getWeatherUseCase.observeWeather(any(), any()) } returns flowOf(dummyWeatherData)
        coEvery { getWeatherUseCase.refreshWeather(any(), any(), any(), any()) } returns Result.Success(dummyWeatherData)

        val viewModel = HomeViewModel(getWeatherUseCase, getActiveLocationUseCase, manageSettingsUseCase)

        // When we force refresh weather and it fails due to network error
        val errorMsg = "Network error"
        coEvery { getWeatherUseCase.refreshWeather(any(), any(), any(), any()) } returns Result.Error(DomainError.NetworkError(errorMsg))

        // Then the error message should be emitted to uiEvent flow and isRefreshing should be false at the end
        viewModel.uiEvent.test {
            viewModel.fetchWeatherForCurrentLocation(forceRefresh = true)
            assertThat(awaitItem()).isEqualTo(errorMsg)
        }

        assertThat(viewModel.isRefreshing.value).isFalse()
    }
}