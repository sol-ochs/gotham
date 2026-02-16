package com.aurox.gotham.data.repository

import com.aurox.gotham.data.local.dao.DeadlineReminderEventDao
import com.aurox.gotham.data.local.dao.TicketDao
import com.aurox.gotham.data.local.entity.TicketEntity
import com.aurox.gotham.data.remote.NycOpenDataApi
import com.aurox.gotham.data.remote.dto.TicketDto
import com.aurox.gotham.domain.model.UsState
import com.aurox.gotham.domain.model.Vehicle
import com.aurox.gotham.domain.util.Result
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TicketRepositoryImplTest {

    private lateinit var ticketDao: TicketDao
    private lateinit var deadlineReminderEventDao: DeadlineReminderEventDao
    private lateinit var api: NycOpenDataApi
    private lateinit var repository: TicketRepositoryImpl

    @Before
    fun setup() {
        ticketDao = mockk()
        deadlineReminderEventDao = mockk()
        api = mockk()
        repository = TicketRepositoryImpl(ticketDao, deadlineReminderEventDao, api)
    }

    @Test
    fun `sync clears paid override when source has caught up`() = runTest {
        val vehicle = testVehicle()
        val updatedTicketSlot = slot<TicketEntity>()
        val existingTicket = existingTicket(isPaidOverride = true, paidOverrideAt = 123456L)

        coEvery { api.getTickets(any(), any(), any(), any()) } returns listOf(
            ticketDto(
                summonsNumber = existingTicket.summonsNumber,
                violationStatus = "PAID",
                amountDue = "0.00"
            )
        )
        coEvery { ticketDao.getSummonsNumbersByVehicle(vehicle.id) } returns listOf(existingTicket.summonsNumber)
        coEvery { ticketDao.getTicketBySummonsNumber(existingTicket.summonsNumber) } returns existingTicket
        coJustRun { ticketDao.updateTicket(capture(updatedTicketSlot)) }

        val result = repository.syncTicketsForVehicle(vehicle)

        assertTrue(result is Result.Success)
        assertEquals(false, updatedTicketSlot.captured.isPaidOverride)
        assertEquals(null, updatedTicketSlot.captured.paidOverrideAt)
        coVerify(exactly = 1) { ticketDao.updateTicket(any()) }
    }

    @Test
    fun `sync keeps paid override when source still unpaid`() = runTest {
        val vehicle = testVehicle()
        val updatedTicketSlot = slot<TicketEntity>()
        val existingTicket = existingTicket(isPaidOverride = true, paidOverrideAt = 123456L)

        coEvery { api.getTickets(any(), any(), any(), any()) } returns listOf(
            ticketDto(
                summonsNumber = existingTicket.summonsNumber,
                violationStatus = "OUTSTANDING",
                amountDue = "65.00"
            )
        )
        coEvery { ticketDao.getSummonsNumbersByVehicle(vehicle.id) } returns listOf(existingTicket.summonsNumber)
        coEvery { ticketDao.getTicketBySummonsNumber(existingTicket.summonsNumber) } returns existingTicket
        coJustRun { ticketDao.updateTicket(capture(updatedTicketSlot)) }

        val result = repository.syncTicketsForVehicle(vehicle)

        assertTrue(result is Result.Success)
        assertEquals(true, updatedTicketSlot.captured.isPaidOverride)
        assertEquals(existingTicket.paidOverrideAt, updatedTicketSlot.captured.paidOverrideAt)
        coVerify(exactly = 1) { ticketDao.updateTicket(any()) }
    }

    private fun testVehicle(): Vehicle {
        return Vehicle(
            id = 1L,
            licensePlate = "ABC1234",
            state = UsState.NY
        )
    }

    private fun existingTicket(isPaidOverride: Boolean, paidOverrideAt: Long?): TicketEntity {
        return TicketEntity(
            summonsNumber = "1234567890",
            vehicleId = 1L,
            plate = "ABC1234",
            state = "NY",
            licenseType = "PAS",
            issueDateTime = "2026-01-01T12:00",
            violation = "NO PARKING",
            violationLocation = "TEST LOCATION",
            fineAmount = 65.0,
            amountDue = 65.0,
            violationStatus = "OUTSTANDING",
            penaltyAmount = null,
            interestAmount = null,
            paymentAmount = null,
            isNew = false,
            isPaidOverride = isPaidOverride,
            paidOverrideAt = paidOverrideAt,
            firstSeenAt = 1000L,
            lastUpdatedAt = 2000L
        )
    }

    private fun ticketDto(
        summonsNumber: String,
        violationStatus: String,
        amountDue: String
    ): TicketDto {
        return TicketDto(
            plate = "ABC1234",
            state = "NY",
            licenseType = "PAS",
            summonsNumber = summonsNumber,
            issueDate = "01/01/2026",
            violationTime = "12:00P",
            violation = "NO PARKING",
            violationLocation = "TEST LOCATION",
            fineAmount = "65.00",
            amountDue = amountDue,
            violationStatus = violationStatus,
            penaltyAmount = null,
            interestAmount = null,
            paymentAmount = null
        )
    }
}
