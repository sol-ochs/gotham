package com.aurox.gotham.domain.usecase.ticket

import com.aurox.gotham.domain.repository.TicketRepository
import com.aurox.gotham.util.Constants.PAYABLE_TICKET_AGE_DAYS
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class GetUnseenTicketCountUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    operator fun invoke(): Flow<Int> {
        val threshold = LocalDateTime.now().minusDays(PAYABLE_TICKET_AGE_DAYS)
        return ticketRepository.getNewTicketCount(threshold)
    }
}
