package com.aurox.gotham.data.repository.mapper

import com.aurox.gotham.testutil.createTicketDto
import org.junit.Assert.assertEquals
import org.junit.Test

class TicketMapperTest {

    @Test
    fun `toEntity converts AM time correctly`() {
        val dto = createTicketDto(issueDate = "01/15/2024", violationTime = "09:30A")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals("2024-01-15T09:30", entity.issueDateTime)
    }

    @Test
    fun `toEntity converts PM time correctly`() {
        val dto = createTicketDto(issueDate = "01/15/2024", violationTime = "05:23P")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals("2024-01-15T17:23", entity.issueDateTime)
    }

    @Test
    fun `toEntity handles noon correctly - 12 PM stays 12`() {
        val dto = createTicketDto(issueDate = "01/15/2024", violationTime = "12:00P")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals("2024-01-15T12:00", entity.issueDateTime)
    }

    @Test
    fun `toEntity handles 12 30 PM correctly`() {
        val dto = createTicketDto(issueDate = "01/15/2024", violationTime = "12:30P")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals("2024-01-15T12:30", entity.issueDateTime)
    }

    @Test
    fun `toEntity handles midnight correctly - 12 AM becomes 00`() {
        val dto = createTicketDto(issueDate = "01/15/2024", violationTime = "12:00A")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals("2024-01-15T00:00", entity.issueDateTime)
    }

    @Test
    fun `toEntity handles 12 45 AM correctly`() {
        val dto = createTicketDto(issueDate = "01/15/2024", violationTime = "12:45A")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals("2024-01-15T00:45", entity.issueDateTime)
    }

    @Test
    fun `toEntity returns epoch for null date`() {
        val dto = createTicketDto(issueDate = null, violationTime = "09:30A")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals("1970-01-01T00:00", entity.issueDateTime)
    }

    @Test
    fun `toEntity returns epoch for blank date`() {
        val dto = createTicketDto(issueDate = "   ", violationTime = "09:30A")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals("1970-01-01T00:00", entity.issueDateTime)
    }

    @Test
    fun `toEntity defaults to 00 00 for null time`() {
        val dto = createTicketDto(issueDate = "01/15/2024", violationTime = null)
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals("2024-01-15T00:00", entity.issueDateTime)
    }

    @Test
    fun `toEntity defaults to 00 00 for blank time`() {
        val dto = createTicketDto(issueDate = "01/15/2024", violationTime = "")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals("2024-01-15T00:00", entity.issueDateTime)
    }

    @Test
    fun `toEntity returns epoch for malformed date`() {
        val dto = createTicketDto(issueDate = "invalid-date", violationTime = "09:30A")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals("1970-01-01T00:00", entity.issueDateTime)
    }

    @Test
    fun `toEntity returns epoch for date with wrong format`() {
        val dto = createTicketDto(issueDate = "2024-01-15", violationTime = "09:30A")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals("1970-01-01T00:00", entity.issueDateTime)
    }

    @Test
    fun `toEntity parses fineAmount string to double`() {
        val dto = createTicketDto(fineAmount = "115.50")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals(115.50, entity.fineAmount, 0.001)
    }

    @Test
    fun `toEntity parses amountDue string to double`() {
        val dto = createTicketDto(amountDue = "230.00")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals(230.00, entity.amountDue, 0.001)
    }

    @Test
    fun `toEntity returns 0 for null fineAmount`() {
        val dto = createTicketDto(fineAmount = null)
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals(0.0, entity.fineAmount, 0.001)
    }

    @Test
    fun `toEntity returns 0 for invalid fineAmount`() {
        val dto = createTicketDto(fineAmount = "not-a-number")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals(0.0, entity.fineAmount, 0.001)
    }

    @Test
    fun `toEntity parses penaltyAmount string to double`() {
        val dto = createTicketDto(penaltyAmount = "25.00")
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals(25.00, entity.penaltyAmount!!, 0.001)
    }

    @Test
    fun `toEntity returns null for null penaltyAmount`() {
        val dto = createTicketDto(penaltyAmount = null)
        val entity = dto.toEntity(vehicleId = 1L)
        assertEquals(null, entity.penaltyAmount)
    }
}
