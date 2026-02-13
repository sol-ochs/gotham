package com.aurox.gotham.domain.usecase.ticket

import com.aurox.gotham.domain.repository.TicketRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetUnpaidReminderDataUseCaseTest {

    private lateinit var ticketRepository: TicketRepository
    private lateinit var useCase: GetUnpaidReminderDataUseCase

    @Before
    fun setup() {
        ticketRepository = mockk()
        useCase = GetUnpaidReminderDataUseCase(ticketRepository)
    }

    @Test
    fun `returns zero count and total when no unpaid reminder tickets`() = runTest {
        coEvery { ticketRepository.getUnpaidReminderTicketCount() } returns 0
        coEvery { ticketRepository.getUnpaidReminderTicketTotal() } returns 0.0

        val result = useCase()

        assertEquals(0, result.count)
        assertEquals(0.0, result.totalAmount, 0.001)
    }

    @Test
    fun `returns correct count and total for multiple tickets`() = runTest {
        coEvery { ticketRepository.getUnpaidReminderTicketCount() } returns 3
        coEvery { ticketRepository.getUnpaidReminderTicketTotal() } returns 245.50

        val result = useCase()

        assertEquals(3, result.count)
        assertEquals(245.50, result.totalAmount, 0.001)
    }

    @Test
    fun `returns correct data for single ticket`() = runTest {
        coEvery { ticketRepository.getUnpaidReminderTicketCount() } returns 1
        coEvery { ticketRepository.getUnpaidReminderTicketTotal() } returns 65.0

        val result = useCase()

        assertEquals(1, result.count)
        assertEquals(65.0, result.totalAmount, 0.001)
    }

    @Test
    fun `handles large total amounts`() = runTest {
        coEvery { ticketRepository.getUnpaidReminderTicketCount() } returns 15
        coEvery { ticketRepository.getUnpaidReminderTicketTotal() } returns 1234.56

        val result = useCase()

        assertEquals(15, result.count)
        assertEquals(1234.56, result.totalAmount, 0.001)
    }
}
