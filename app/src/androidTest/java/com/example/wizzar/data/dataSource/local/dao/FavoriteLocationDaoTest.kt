package com.example.wizzar.data.dataSource.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.example.wizzar.data.dataSource.local.db.WeatherDatabase
import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class FavoriteLocationDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dao: FavoriteLocationDao

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

    @Before
    fun setup() {
        // In-memory database for isolated, fast, and safe testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.favoriteLocationDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertFavorite_validEntity_savesToDatabase() = runTest {
        // Given - A valid FavoriteLocationEntity (dummyFavorite1)
        dao.insertFavorite(dummyFavorite1)

        // When - We observe the favorites list
        dao.observeAllFavorites().test {
            val favoritesList = awaitItem()
            // Then - The list should contain our inserted favorite with correct data
            assertThat(favoritesList).hasSize(1)
            assertThat(favoritesList.first().cityName).isEqualTo("Cairo")
            assertThat(favoritesList.first().latitude).isEqualTo(30.044)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteFavorite_existingFavorite_removesFromDatabase() = runTest {
        // Given - An existing favorite in the database
        dao.insertFavorite(dummyFavorite1)

        // When - We delete that favorite by its coordinates
        dao.deleteFavorite(latitude = 30.044, longitude = 31.235)

        // Then - Observing the favorites list should yield an empty list
        dao.observeAllFavorites().test {
            val favoritesList = awaitItem()
            assertThat(favoritesList).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * TEST CASE 3: observeAllFavorites()
     * What we do: Insert multiple favorites and verify the Flow emits them all.
     * Why: Proves Room can retrieve and map multiple rows into our Entity objects perfectly.
     */
    @Test
    fun observeAllFavorites_multipleFavoritesInserted_emitsCompleteList() = runTest {
        // Given - Multiple favorites inserted into the database
        dao.insertFavorite(dummyFavorite1)
        dao.insertFavorite(dummyFavorite2)

        // When - We observe all favorites through the Flow
        dao.observeAllFavorites().test {
            val favoritesList = awaitItem()

            assertThat(favoritesList).hasSize(2)

            // Extract the city names to easily verify both made it into the DB
            val cityNames = favoritesList.map { it.cityName }
            assertThat(cityNames).containsExactly("Cairo", "Paris")

            cancelAndIgnoreRemainingEvents()
        }
    }
}