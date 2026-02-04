package com.aurox.gotham.domain.model

import com.aurox.gotham.testutil.createTicket
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TicketTest {

    @Test
    fun `isPaid returns true when status contains PAID`() {
        val ticket = createTicket(violationStatus = "PAID", amountDue = 65.0)
        assertTrue(ticket.isPaid)
    }

    @Test
    fun `isPaid returns true when status contains paid lowercase`() {
        val ticket = createTicket(violationStatus = "paid", amountDue = 65.0)
        assertTrue(ticket.isPaid)
    }

    @Test
    fun `isPaid returns true for HEARING HELD-PAID status`() {
        val ticket = createTicket(violationStatus = "HEARING HELD-PAID", amountDue = 65.0)
        assertTrue(ticket.isPaid)
    }

    @Test
    fun `isPaid returns false for HEARING HELD without PAID`() {
        val ticket = createTicket(violationStatus = "HEARING HELD", amountDue = 65.0)
        assertFalse(ticket.isPaid)
    }

    @Test
    fun `isPaid returns false when status is null and amountDue is positive`() {
        val ticket = createTicket(violationStatus = null, amountDue = 65.0)
        assertFalse(ticket.isPaid)
    }

    @Test
    fun `isPaid returns true when amountDue is zero`() {
        val ticket = createTicket(violationStatus = null, amountDue = 0.0)
        assertTrue(ticket.isPaid)
    }

    @Test
    fun `isPaid returns true when amountDue is negative`() {
        val ticket = createTicket(violationStatus = null, amountDue = -10.0)
        assertTrue(ticket.isPaid)
    }

    @Test
    fun `isPaid returns true when status is empty but amountDue is zero`() {
        val ticket = createTicket(violationStatus = "", amountDue = 0.0)
        assertTrue(ticket.isPaid)
    }
}
