package com.gotham.app.domain.usecase.ticket

import com.gotham.app.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnseenTicketCountUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    operator fun invoke(): Flow<Int> {
        return ticketRepository.getNewTicketCount()
    }
}
