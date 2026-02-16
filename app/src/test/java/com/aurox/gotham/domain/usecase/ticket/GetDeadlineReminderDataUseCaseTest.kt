package com.aurox.gotham.domain.usecase.ticket

import com.aurox.gotham.domain.model.Ticket
import com.aurox.gotham.domain.repository.TicketRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class GetDeadlineReminderDataUseCaseTest {

    private lateinit var ticketRepository: TicketRepository
    private lateinit var useCase: GetDeadlineReminderDataUseCase

    @Before
    fun setup() {
        ticketRepository = mockk()
        useCase = GetDeadlineReminderDataUseCase(ticketRepository)
    }

    @Test
    fun `returns zero when no milestone matches`() = runTest {
        val today = LocalDate.of(2026, 2, 13)
        val tickets = listOf(
            ticket("A", today.minusDays(20).atStartOfDay()),
            ticket("B", today.minusDays(24).atStartOfDay())
        )

        coEvery { ticketRepository.getDeadlineReminderCandidates(today) } returns tickets

        val result = useCase(today)

        assertEquals(0, result.count)
        assertEquals(0, result.nearestDaysLeft)
        assertTrue(result.targets.isEmpty())
    }

    @Test
    fun `includes unsent milestone tickets and computes nearest days left`() = runTest {
        val today = LocalDate.of(2026, 2, 13)
        val tickets = listOf(
            ticket("A", today.minusDays(23).atStartOfDay()),
            ticket("B", today.minusDays(29).atStartOfDay())
        )

        coEvery { ticketRepository.getDeadlineReminderCandidates(today) } returns tickets
        coEvery { ticketRepository.hasDeadlineReminderEvent("A", 23) } returns false
        coEvery { ticketRepository.hasDeadlineReminderEvent("B", 29) } returns false

        val result = useCase(today)

        assertEquals(2, result.count)
        assertEquals(1, result.nearestDaysLeft)
        assertEquals(2, result.targets.size)
    }

    @Test
    fun `skips already sent milestones`() = runTest {
        val today = LocalDate.of(2026, 2, 13)
        val tickets = listOf(ticket("A", today.minusDays(27).atStartOfDay()))

        coEvery { ticketRepository.getDeadlineReminderCandidates(today) } returns tickets
        coEvery { ticketRepository.hasDeadlineReminderEvent("A", 27) } returns true

        val result = useCase(today)

        assertEquals(0, result.count)
        assertTrue(result.targets.isEmpty())
    }

    private fun ticket(
        summonsNumber: String,
        issueDateTime: LocalDateTime
    ): Ticket {
        return Ticket(
            summonsNumber = summonsNumber,
            vehicleId = 1L,
            plate = "ABC1234",
            state = "NY",
            licenseType = "PAS",
            issueDateTime = issueDateTime,
            violation = "NO PARKING",
            violationLocation = null,
            fineAmount = 65.0,
            amountDue = 65.0,
            violationStatus = "OUTSTANDING",
            penaltyAmount = null,
            interestAmount = null,
            paymentAmount = null,
            isNew = false
        )
    }
}
