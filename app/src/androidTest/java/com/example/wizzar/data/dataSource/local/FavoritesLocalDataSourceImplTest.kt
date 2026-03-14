package com.example.wizzar.data.dataSource.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
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
@MediumTest
class FavoritesLocalDataSourceImplTest {

    private lateinit var database: WeatherDatabase
    private lateinit var localDataSource: FavoritesLocalDataSourceImpl

    // Dummy Data
    private val dummyFavoriteEntity = FavoriteLocationEntity(
        latitude = 30.044,
        longitude = 31.235,
        cityName = "Cairo"
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        localDataSource = FavoritesLocalDataSourceImpl(database.favoriteLocationDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertFavorite_validEntity_savesToDatabase() = runTest {
        // Given: A favorite entity to save
        val favorite = dummyFavoriteEntity

        // When: Inserting into the local data source
        localDataSource.insertFavorite(favorite)

        // Then: The flow emits the inserted entity
        localDataSource.observeAllFavorites().test {
            val list = awaitItem()
            assertThat(list).hasSize(1)
            assertThat(list.first().cityName).isEqualTo("Cairo")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteFavorite_existingCoordinates_removesFromDatabase() = runTest {
        // Given: A favorite is already saved
        localDataSource.insertFavorite(dummyFavoriteEntity)

        // When: Deleting by coordinates
        localDataSource.deleteFavorite(lat = 30.044, lon = 31.235)

        // Then: The resulting flow should be empty
        localDataSource.observeAllFavorites().test {
            val list = awaitItem()
            assertThat(list).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeAllFavorites_multipleFavoritesInserted_emitsCompleteList() = runTest {
        // Given: Multiple favorites are inserted
        val favorite2 = FavoriteLocationEntity(latitude = 48.856, longitude = 2.352, cityName = "Paris")
        localDataSource.insertFavorite(dummyFavoriteEntity)
        localDataSource.insertFavorite(favorite2)

        // When: Observing all favorites
        localDataSource.observeAllFavorites().test {
            // Then: The emitted list should contain both favorites with correct data
            val list = awaitItem()

            assertThat(list).hasSize(2)
            assertThat(list.map { it.cityName }).containsExactly("Cairo", "Paris")

            cancelAndIgnoreRemainingEvents()
        }
    }
}