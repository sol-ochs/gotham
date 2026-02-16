package com.aurox.gotham.domain.usecase.ticket

import com.aurox.gotham.domain.repository.TicketRepository
import javax.inject.Inject

data class UnpaidReminderData(
    val count: Int,
    val totalAmount: Double
)

class GetUnpaidReminderDataUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(): UnpaidReminderData {
        val count = ticketRepository.getUnpaidReminderTicketCount()
        val total = ticketRepository.getUnpaidReminderTicketTotal()
        return UnpaidReminderData(count, total)
    }
}
