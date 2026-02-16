package com.aurox.gotham.domain.model

import com.aurox.gotham.testutil.createTicket
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TicketTest {

    @Test
    fun `isPaid returns false when amountDue is positive regardless of adjudication status`() {
        val ticket = createTicket(adjudicationStatus = "HEARING PENDING", amountDue = 65.0)
        assertFalse(ticket.isPaid)
    }

    @Test
    fun `isPaid returns false when adjudication status is null and amountDue is positive`() {
        val ticket = createTicket(adjudicationStatus = null, amountDue = 65.0)
        assertFalse(ticket.isPaid)
    }

    @Test
    fun `isPaid returns true when amountDue is zero`() {
        val ticket = createTicket(adjudicationStatus = null, amountDue = 0.0)
        assertTrue(ticket.isPaid)
    }

    @Test
    fun `isPaid returns true when amountDue is negative`() {
        val ticket = createTicket(adjudicationStatus = null, amountDue = -10.0)
        assertTrue(ticket.isPaid)
    }

    @Test
    fun `isPaid returns true when adjudication status is empty but amountDue is zero`() {
        val ticket = createTicket(adjudicationStatus = "", amountDue = 0.0)
        assertTrue(ticket.isPaid)
    }

    @Test
    fun `effective amount is zero for user marked paid tickets`() {
        val ticket = createTicket(
            adjudicationStatus = "HEARING PENDING",
            amountDue = 65.0,
            isNew = false
        ).copy(isPaidOverride = true)

        assertEquals(0.0, ticket.effectiveAmountDue, 0.0)
    }
}
