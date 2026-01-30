package com.aurox.gotham.presentation.ticket.detail

import com.aurox.gotham.domain.model.Ticket

data class TicketDetailState(
    val ticket: Ticket? = null,
    val isLoading: Boolean = true
)
