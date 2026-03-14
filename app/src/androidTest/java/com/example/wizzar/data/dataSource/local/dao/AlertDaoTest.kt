package com.example.wizzar.data.dataSource.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
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
@SmallTest
class AlertDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dao: AlertDao

    // Dummy Data
    private val dummyAlert1 = AlertEntity(
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

    private val dummyAlert2 = AlertEntity(
        id = "alert-2",
        startTime = 600L,
        endTime = 1200L,
        isAlarmSound = false,
        latitude = 48.856,
        longitude = 2.352,
        cityName = "Paris",
        isActive = false,
        snoozedUntil = null,
        lastTriggeredDate = null
    )

    @Before
    fun setup() {
        // Creates a temporary database in RAM that allows queries on the main thread just for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.alertDao()
    }

    @After
    fun teardown() {
        // Destroys the database after every test to ensure a clean slate
        database.close()
    }

    @Test
    fun insertAlert_validEntity_savesToDatabase() = runTest {
        // When: Inserting a valid AlertEntity into the database
        dao.insertAlert(dummyAlert1)

        // Then: We should be able to retrieve it by its ID and all fields should match
        val retrievedAlert = dao.getAlertById(dummyAlert1.id)
        assertThat(retrievedAlert).isNotNull()
        assertThat(retrievedAlert?.cityName).isEqualTo("Cairo")
        assertThat(retrievedAlert?.id).isEqualTo(dummyAlert1.id)
    }

    @Test
    fun deleteAlert_existingAlert_removesFromDatabase() = runTest {
        // Given: An existing alert in the database
        dao.insertAlert(dummyAlert1)
        assertThat(dao.getAlertById(dummyAlert1.id)).isNotNull() // Ensure it was added

        // When: Deleting that alert
        dao.deleteAlert(dummyAlert1.id)

        // Then: It should no longer be retrievable from the database
        val retrievedAlert = dao.getAlertById(dummyAlert1.id)
        assertThat(retrievedAlert).isNull() // Ensure it is gone
    }

    @Test
    fun observeAllAlerts_multipleAlertsInserted_emitsCompleteList() = runTest {
        // Given: Multiple alerts inserted into the database
        dao.insertAlert(dummyAlert1)
        dao.insertAlert(dummyAlert2)

        // When: Observing all alerts through the Flow
        dao.observeAllAlerts().test {
            // Then: We should receive a list containing all the inserted alerts
            val alertsList = awaitItem()

            assertThat(alertsList).hasSize(2)
            assertThat(alertsList.map { it.id }).containsExactly("alert-1", "alert-2")

            cancelAndIgnoreRemainingEvents()
        }
    }
}