package com.aurox.gotham.domain.usecase.ticket

import com.aurox.gotham.domain.model.Ticket
import com.aurox.gotham.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTicketsUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    operator fun invoke(): Flow<List<Ticket>> {
        return ticketRepository.getAllTickets()
    }
}
