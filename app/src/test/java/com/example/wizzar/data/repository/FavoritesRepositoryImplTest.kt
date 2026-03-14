package com.example.wizzar.data.repository

import app.cash.turbine.test
import com.example.wizzar.data.dataSource.local.FavoritesLocalDataSource
import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FavoritesRepositoryImplTest {

    // 1. The Mock
    private val localDataSource: FavoritesLocalDataSource = mockk()

    // 2. The Class Under Test
    private lateinit var repository: FavoritesRepositoryImpl

    // 3. Dummies
    private val dummyLat = 30.044
    private val dummyLon = 31.235
    private val dummyCityName = "Cairo"
    private val dummyEntity = FavoriteLocationEntity(
        latitude = dummyLat,
        longitude = dummyLon,
        cityName = dummyCityName
    )

    @Before
    fun setup() {
        // Instantiate the repository with our mock
        repository = FavoritesRepositoryImpl(localDataSource)
    }

    @Test
    fun observeFavorites_hasCachedData_emitsFavoritesList() = runTest {
        // Given - Setup the mock to return a flow that emits a list containing our dummy entity
        val dummyList = listOf(dummyEntity)
        every { localDataSource.observeAllFavorites() } returns flowOf(dummyList)

        // When - Collect the flow from the repository
        repository.observeFavorites().test {
            // Then - Assert that the emitted item is equal to our dummy list
            val emittedItem = awaitItem()
            assertThat(emittedItem).isEqualTo(dummyList)
            awaitComplete()
        }
    }

    @Test
    fun addFavorite_validInput_callsInsertWithCorrectEntity() = runTest {
        // Given - Setup the mock to expect an insert call and return Unit
        coEvery { localDataSource.insertFavorite(any()) } returns Unit

        // When - Call addFavorite with our dummy data
        repository.addFavorite(lat = dummyLat, lon = dummyLon, cityName = dummyCityName)

        // Then - Verify that insertFavorite was called exactly once with an entity that has the correct data
        coVerify(exactly = 1) {
            localDataSource.insertFavorite(
                FavoriteLocationEntity(
                    latitude = dummyLat,
                    longitude = dummyLon,
                    cityName = dummyCityName
                )
            )
        }
    }

    @Test
    fun removeFavorite_validCoordinates_callsDeleteFavorite() = runTest {
        // When - Setup the mock to expect a delete call and return Unit
        coEvery { localDataSource.deleteFavorite(any(), any()) } returns Unit

        // When - Call removeFavorite with our dummy coordinates
        repository.removeFavorite(lat = dummyLat, lon = dummyLon)

        // Then - Verify that deleteFavorite was called exactly once with the correct coordinates
        coVerify(exactly = 1) {
            localDataSource.deleteFavorite(lat = dummyLat, lon = dummyLon)
        }
    }
}