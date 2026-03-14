package com.example.wizzar.data.dataSource.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.example.wizzar.data.dataSource.local.db.WeatherDatabase
import com.example.wizzar.data.dataSource.local.entity.AlertEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class AlertsLocalDataSourceImplTest {

    private lateinit var database: WeatherDatabase
    private lateinit var localDataSource: AlertsLocalDataSourceImpl

    // Dummy Data
    private val dummyAlertEntity = AlertEntity(
        id = "alert-1",
        startTime = 480L,
        endTime = 1020L,
        isAlarmSound = true,
        latitude = 30.044,
        longitude = 31.235,
        cityName = "Cairo",
        isActive = true,
        snoozedUntil = null,
        lastTriggeredDate = null
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        localDataSource = AlertsLocalDataSourceImpl(database.alertDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAlert_validEntity_savesToDatabase() = runTest {
        // Given: An alert entity to save
        val alert = dummyAlertEntity

        // When: Inserting into the local data source
        localDataSource.insertAlert(alert)

        // Then: The loaded data matches the original
        val loaded = localDataSource.getAlertById("alert-1")
        assertThat(loaded).isNotNull()
        assertThat(loaded?.cityName).isEqualTo("Cairo")
        assertThat(loaded?.isAlarmSound).isTrue()
    }

    @Test
    fun deleteAlert_existingAlertId_removesFromDatabase() = runTest {
        // Given: An alert is already saved
        localDataSource.insertAlert(dummyAlertEntity)
        assertThat(localDataSource.getAlertById("alert-1")).isNotNull()

        // When: Deleting the alert
        localDataSource.deleteAlert("alert-1")

        // Then: The alert should no longer exist
        val loaded = localDataSource.getAlertById("alert-1")
        assertThat(loaded).isNull()
    }

    @Test
    fun observeAllAlerts_multipleAlertsInserted_emitsCompleteList() = runTest {
        // Given: Multiple alerts in the database
        val alert2 = dummyAlertEntity.copy(id = "alert-2", cityName = "Paris")
        localDataSource.insertAlert(dummyAlertEntity)
        localDataSource.insertAlert(alert2)

        // When: Observing all alerts through the Flow
        localDataSource.observeAllAlerts().test {
            // Then: The emitted list should contain both alerts with correct data
            val alertsList = awaitItem()

            assertThat(alertsList).hasSize(2)
            assertThat(alertsList.map { it.id }).containsExactly("alert-1", "alert-2")

            cancelAndIgnoreRemainingEvents()
        }
    }
}