package com.aurox.gotham.domain.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Ticket(
    val summonsNumber: String,
    val vehicleId: Long,
    val plate: String,
    val state: String,
    val licenseType: String?,
    val issueDateTime: LocalDateTime,
    val violation: String,
    val violationLocation: String?,
    val fineAmount: Double,
    val amountDue: Double,
    val adjudicationStatus: String?,
    val penaltyAmount: Double?,
    val interestAmount: Double?,
    val paymentAmount: Double?,
    val isNew: Boolean = true,
    val isPaidOverride: Boolean = false,
    val paidOverrideAt: Long? = null,
    val firstSeenAt: Long = System.currentTimeMillis(),
    val lastUpdatedAt: Long = System.currentTimeMillis()
) {
    val isPaid: Boolean
        get() = isPaidOverride || amountDue <= 0.0

    val effectiveAmountDue: Double
        get() = if (isPaid) 0.0 else amountDue

    val formattedIssueDate: String
        get() = issueDateTime.format(DATE_FORMATTER)

    val formattedIssueTime: String
        get() = issueDateTime.format(TIME_FORMATTER)

    val formattedFineAmount: String
        get() = "$${String.format(Locale.US, "%.2f", fineAmount)}"

    val formattedEffectiveAmountDue: String
        get() = "$${String.format(Locale.US, "%.2f", effectiveAmountDue)}"

    val formattedViolation: String
        get() = ViolationType.getDisplayName(violation)

    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        private val TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a")
    }
}
