package com.example.wizzar.data.repository

import app.cash.turbine.test
import com.example.wizzar.data.dataSource.local.AlertsLocalDataSource
import com.example.wizzar.data.dataSource.local.entity.AlertEntity
import com.example.wizzar.domain.model.WeatherAlert
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AlertsRepositoryImplTest {

    // 1. The Mock
    private val localDataSource: AlertsLocalDataSource = mockk()

    // 2. The Class Under Test
    private lateinit var repository: AlertsRepositoryImpl

    // 3. Dummy Data
    private val dummyId = "test-alert-id"

    // Note: Ensure these parameters perfectly match your actual Domain/Entity constructors!
    private val dummyEntity = AlertEntity(
        id = dummyId,
        startTime = 480L, // 8:00 AM in minutes
        endTime = 1020L,  // 5:00 PM in minutes
        isAlarmSound = true,
        latitude = 30.044,
        longitude = 31.235,
        cityName = "Cairo",
        isActive = true,
        snoozedUntil = null,
        lastTriggeredDate = null
    )

    private val dummyDomainAlert = WeatherAlert(
        id = dummyId,
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
        repository = AlertsRepositoryImpl(localDataSource)
    }

    @Test
    fun observeAlerts_hasAlerts_emitsMappedDomainList() = runTest {
        // Given - Setup the mock to return a flow that emits a list containing our dummy entity
        val entityList = listOf(dummyEntity)
        every { localDataSource.observeAllAlerts() } returns flowOf(entityList)

        // When - Collect the flow from the repository
        repository.observeAlerts().test {
            val resultList = awaitItem()

            // Then - Assert that the emitted list is not empty and that the first item's ID matches our dummy ID, proving the mapper worked
            assertThat(resultList).isNotEmpty()
            assertThat(resultList.first().id).isEqualTo(dummyId)
            awaitComplete()
        }
    }

    @Test
    fun saveAlert_validAlert_callsInsertOnDataSource() = runTest {
        // Given - Setup the mock to expect an insert call and return Unit
        coEvery { localDataSource.insertAlert(any()) } returns Unit

        // When - Call saveAlert with our dummy domain alert
        repository.saveAlert(dummyDomainAlert)

        // Then - Verify that insertAlert was called exactly once with an entity that has the correct data, proving the mapper successfully mapped the Domain object to the Entity object
        coVerify(exactly = 1) {
            localDataSource.insertAlert(withArg { entityToInsert ->
                // Prove the mapper successfully mapped the Domain ID to the Entity ID
                assertThat(entityToInsert.id).isEqualTo(dummyId)
                assertThat(entityToInsert.cityName).isEqualTo("Cairo")
            })
        }
    }

    @Test
    fun deleteAlert_validId_callsDeleteOnDataSource() = runTest {
        // Given - Setup the mock to expect a delete call and return Unit
        coEvery { localDataSource.deleteAlert(any()) } returns Unit

        // When - Call deleteAlert with our dummy ID
        repository.deleteAlert(dummyId)

        // Then - Verify that deleteAlert was called exactly once with the correct ID
        coVerify(exactly = 1) {
            localDataSource.deleteAlert(dummyId)
        }
    }
}