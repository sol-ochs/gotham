package com.aurox.gotham.domain.usecase.ticket

import com.aurox.gotham.domain.repository.TicketRepository
import javax.inject.Inject

class MarkTicketAsSeenUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(summonsNumber: String) {
        ticketRepository.markTicketAsSeen(summonsNumber)
    }
}
