package com.example.wizzar.presentation.favorites

import app.cash.turbine.test
import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import com.example.wizzar.domain.usecase.ManageFavoritesUseCase
import com.example.wizzar.utils.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mocked Dependency
    private val manageFavoritesUseCase: ManageFavoritesUseCase = mockk()

    // Class under test
    private lateinit var viewModel: FavoritesViewModel

    // Dummy Data
    private val dummyFavorite1 = FavoriteLocationEntity(
        latitude = 30.044,
        longitude = 31.235,
        cityName = "Cairo"
    )
    private val dummyFavorite2 = FavoriteLocationEntity(
        latitude = 48.856,
        longitude = 2.352,
        cityName = "Paris"
    )
    private val dummyFavoritesList = listOf(dummyFavorite1, dummyFavorite2)

    @Before
    fun setup() {
        // Given - Setup Mock for the observeFavorites flow to emit our dummy list
        every { manageFavoritesUseCase.observeFavorites() } returns flowOf(dummyFavoritesList)

        // When - Initialize the ViewModel with the mocked UseCase
        viewModel = FavoritesViewModel(manageFavoritesUseCase)
    }

    @Test
    fun favoritesList_onCollect_emitsFavoritesFromUseCase() = runTest {
        // When - Collect the favoritesList StateFlow
        viewModel.favoritesList.test {
            // Then - Assert - The first emission should be our dummy list (or empty if the flow emits before the data is ready)
            val firstEmission = awaitItem()
            if (firstEmission.isEmpty()) {
                assertThat(awaitItem()).isEqualTo(dummyFavoritesList)
            } else {
                assertThat(firstEmission).isEqualTo(dummyFavoritesList)
            }
        }
    }

    @Test
    fun removeFavorite_calledWithFavorite_callsUseCaseRemove() = runTest {
        // Given - Setup Mock for the specific suspend function
        coEvery { manageFavoritesUseCase.removeFavorite(any(), any()) } returns Unit

        // When - Call the function under test with our dummy favorite
        viewModel.removeFavorite(dummyFavorite1)

        // Then - Assert - Verify the UseCase was called to remove the exact location
        coVerify(exactly = 1) {
            manageFavoritesUseCase.removeFavorite(
                lat = 30.044,
                lon = 31.235
            )
        }
    }

    @Test
    fun undoRemoveFavorite_calledWithFavorite_callsUseCaseAddLocation() = runTest {
        //Given - Setup Mock for the specific suspend function
        coEvery { manageFavoritesUseCase.addFavoriteLocation(any(), any(), any()) } returns Unit

        // When - Call the function under test with our dummy favorite
        viewModel.undoRemoveFavorite(dummyFavorite2)

        // Then - Assert - Verify the UseCase was called to add the exact location with the correct city name
        coVerify(exactly = 1) {
            manageFavoritesUseCase.addFavoriteLocation(
                lat = 48.856,
                lon = 2.352,
                cityName = "Paris"
            )
        }
    }
}