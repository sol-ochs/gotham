package com.gotham.app.domain.usecase.ticket

import com.gotham.app.domain.repository.TicketRepository
import javax.inject.Inject

class MarkTicketAsSeenUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(summonsNumber: String) {
        ticketRepository.markTicketAsSeen(summonsNumber)
    }
}
