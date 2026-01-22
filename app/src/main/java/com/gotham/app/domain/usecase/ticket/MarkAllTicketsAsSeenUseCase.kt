package com.gotham.app.domain.usecase.ticket

import com.gotham.app.domain.repository.TicketRepository
import javax.inject.Inject

class MarkAllTicketsAsSeenUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke() {
        ticketRepository.markAllTicketsAsSeen()
    }
}
