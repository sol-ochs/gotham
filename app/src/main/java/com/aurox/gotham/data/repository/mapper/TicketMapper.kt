package com.aurox.gotham.data.repository.mapper

import com.aurox.gotham.data.local.entity.TicketEntity
import com.aurox.gotham.data.remote.dto.TicketDto
import com.aurox.gotham.domain.model.Ticket
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

fun TicketEntity.toDomain(): Ticket {
    return Ticket(
        summonsNumber = summonsNumber,
        vehicleId = vehicleId,
        plate = plate,
        state = state,
        licenseType = licenseType,
        issueDateTime = LocalDateTime.parse(issueDateTime, ISO_FORMATTER),
        violation = violation,
        violationLocation = violationLocation,
        fineAmount = fineAmount,
        amountDue = amountDue,
        violationStatus = violationStatus,
        penaltyAmount = penaltyAmount,
        interestAmount = interestAmount,
        paymentAmount = paymentAmount,
        isNew = isNew,
        firstSeenAt = firstSeenAt,
        lastUpdatedAt = lastUpdatedAt
    )
}

fun Ticket.toEntity(): TicketEntity {
    return TicketEntity(
        summonsNumber = summonsNumber,
        vehicleId = vehicleId,
        plate = plate,
        state = state,
        licenseType = licenseType,
        issueDateTime = issueDateTime.format(ISO_FORMATTER),
        violation = violation,
        violationLocation = violationLocation,
        fineAmount = fineAmount,
        amountDue = amountDue,
        violationStatus = violationStatus,
        penaltyAmount = penaltyAmount,
        interestAmount = interestAmount,
        paymentAmount = paymentAmount,
        isNew = isNew,
        firstSeenAt = firstSeenAt,
        lastUpdatedAt = lastUpdatedAt
    )
}

private fun convertToIso8601(dateStr: String?, timeStr: String?): String {
    if (dateStr.isNullOrBlank()) return "1970-01-01T00:00"

    // Convert MM/DD/YYYY to YYYY-MM-DD
    val dateParts = dateStr.split("/")
    if (dateParts.size != 3) return "1970-01-01T00:00"

    val datePart = try {
        "${dateParts[2]}-${dateParts[0].padStart(2, '0')}-${dateParts[1].padStart(2, '0')}"
    } catch (e: Exception) {
        return "1970-01-01T00:00"
    }

    // Convert time like "05:23P" or "12:45A" to 24-hour "HH:MM"
    val timePart = if (!timeStr.isNullOrBlank() && timeStr.contains(":")) {
        val parts = timeStr.split(":")
        val hourStr = parts[0]
        val minuteAndSuffix = parts[1]
        val minuteStr = minuteAndSuffix.filter { it.isDigit() }
        val suffix = minuteAndSuffix.lastOrNull { it.isLetter() }?.uppercaseChar()

        val hour = hourStr.toIntOrNull() ?: 0
        val adjustedHour = when {
            suffix == 'P' && hour < 12 -> hour + 12
            suffix == 'A' && hour == 12 -> 0
            else -> hour
        }
        String.format(Locale.US, "%02d:%02d", adjustedHour, minuteStr.toIntOrNull() ?: 0)
    } else {
        "00:00"
    }

    return "${datePart}T${timePart}"
}

fun TicketDto.toEntity(vehicleId: Long): TicketEntity {
    return TicketEntity(
        summonsNumber = summonsNumber,
        vehicleId = vehicleId,
        plate = plate ?: "",
        state = state ?: "",
        licenseType = licenseType,
        issueDateTime = convertToIso8601(issueDate, violationTime),
        violation = violation ?: "Unknown Violation",
        violationLocation = violationLocation,
        fineAmount = fineAmount?.toDoubleOrNull() ?: 0.0,
        amountDue = amountDue?.toDoubleOrNull() ?: 0.0,
        violationStatus = violationStatus,
        penaltyAmount = penaltyAmount?.toDoubleOrNull(),
        interestAmount = interestAmount?.toDoubleOrNull(),
        paymentAmount = paymentAmount?.toDoubleOrNull(),
        isNew = true,
        firstSeenAt = System.currentTimeMillis(),
        lastUpdatedAt = System.currentTimeMillis()
    )
}

fun List<TicketEntity>.toDomainList(): List<Ticket> {
    return map { it.toDomain() }
}

fun List<TicketDto>.toEntityList(vehicleId: Long): List<TicketEntity> {
    return map { it.toEntity(vehicleId) }
}
