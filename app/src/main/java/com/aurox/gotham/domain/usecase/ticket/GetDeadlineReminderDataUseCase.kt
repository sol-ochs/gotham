package com.aurox.gotham.domain.usecase.ticket

import com.aurox.gotham.domain.repository.TicketRepository
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class DeadlineReminderTarget(
    val summonsNumber: String,
    val milestoneDay: Int
)

data class DeadlineReminderData(
    val count: Int,
    val nearestDaysLeft: Int,
    val targets: List<DeadlineReminderTarget>
)

class GetDeadlineReminderDataUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(today: LocalDate = LocalDate.now()): DeadlineReminderData {
        val milestones = setOf(23, 27, 29)
        val tickets = ticketRepository.getDeadlineReminderCandidates(today)

        val targets = mutableListOf<DeadlineReminderTarget>()
        var nearestDaysLeft = Int.MAX_VALUE

        tickets.forEach { ticket ->
            if (ticket.isPaid) return@forEach

            val daysSinceIssue = ChronoUnit.DAYS.between(ticket.issueDateTime.toLocalDate(), today).toInt()
            if (daysSinceIssue !in milestones) return@forEach

            val alreadySent = ticketRepository.hasDeadlineReminderEvent(ticket.summonsNumber, daysSinceIssue)
            if (alreadySent) return@forEach

            targets.add(
                DeadlineReminderTarget(
                    summonsNumber = ticket.summonsNumber,
                    milestoneDay = daysSinceIssue
                )
            )

            val daysLeft = 30 - daysSinceIssue
            if (daysLeft < nearestDaysLeft) {
                nearestDaysLeft = daysLeft
            }
        }

        if (targets.isEmpty()) {
            return DeadlineReminderData(
                count = 0,
                nearestDaysLeft = 0,
                targets = emptyList()
            )
        }

        return DeadlineReminderData(
            count = targets.size,
            nearestDaysLeft = nearestDaysLeft,
            targets = targets
        )
    }
}
