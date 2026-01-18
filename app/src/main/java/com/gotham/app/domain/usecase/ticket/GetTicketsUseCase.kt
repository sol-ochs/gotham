package com.gotham.app.domain.usecase.ticket

import com.gotham.app.domain.model.Ticket
import com.gotham.app.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTicketsUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    operator fun invoke(): Flow<List<Ticket>> {
        return ticketRepository.getAllTickets()
    }
}
