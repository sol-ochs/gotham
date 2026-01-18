package com.gotham.app.presentation.ticket.detail

import com.gotham.app.domain.model.Ticket

data class TicketDetailState(
    val ticket: Ticket? = null,
    val isLoading: Boolean = true
)
