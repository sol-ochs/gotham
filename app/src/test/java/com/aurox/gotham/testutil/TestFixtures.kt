package com.aurox.gotham.testutil

import com.aurox.gotham.data.remote.dto.TicketDto
import com.aurox.gotham.domain.model.Ticket
import com.aurox.gotham.domain.model.UsState
import com.aurox.gotham.domain.model.Vehicle
import java.time.LocalDateTime

fun createTicket(
    summonsNumber: String = "1234567890",
    vehicleId: Long = 1L,
    plate: String = "ABC1234",
    state: String = "NY",
    licenseType: String? = "PAS",
    issueDateTime: LocalDateTime = LocalDateTime.of(2024, 1, 15, 14, 30),
    violation: String = "NO PARKING-STREET CLEANING",
    violationLocation: String? = "123 MAIN ST",
    fineAmount: Double = 65.0,
    amountDue: Double = 65.0,
    adjudicationStatus: String? = null,
    penaltyAmount: Double? = null,
    interestAmount: Double? = null,
    paymentAmount: Double? = null,
    isNew: Boolean = true,
    firstSeenAt: Long = 1000L,
    lastUpdatedAt: Long = 1000L
) = Ticket(
    summonsNumber = summonsNumber,
    vehicleId = vehicleId,
    plate = plate,
    state = state,
    licenseType = licenseType,
    issueDateTime = issueDateTime,
    violation = violation,
    violationLocation = violationLocation,
    fineAmount = fineAmount,
    amountDue = amountDue,
    adjudicationStatus = adjudicationStatus,
    penaltyAmount = penaltyAmount,
    interestAmount = interestAmount,
    paymentAmount = paymentAmount,
    isNew = isNew,
    firstSeenAt = firstSeenAt,
    lastUpdatedAt = lastUpdatedAt
)

fun createTicketDto(
    summonsNumber: String = "1234567890",
    plate: String? = "ABC1234",
    state: String? = "NY",
    licenseType: String? = "PAS",
    issueDate: String? = "01/15/2024",
    violationTime: String? = "02:30P",
    violation: String? = "NO PARKING-STREET CLEANING",
    violationLocation: String? = "123 MAIN ST",
    fineAmount: String? = "65.00",
    amountDue: String? = "65.00",
    adjudicationStatus: String? = null,
    penaltyAmount: String? = null,
    interestAmount: String? = null,
    paymentAmount: String? = null
) = TicketDto(
    summonsNumber = summonsNumber,
    plate = plate,
    state = state,
    licenseType = licenseType,
    issueDate = issueDate,
    violationTime = violationTime,
    violation = violation,
    violationLocation = violationLocation,
    fineAmount = fineAmount,
    amountDue = amountDue,
    adjudicationStatus = adjudicationStatus,
    penaltyAmount = penaltyAmount,
    interestAmount = interestAmount,
    paymentAmount = paymentAmount
)

fun createVehicle(
    id: Long = 0L,
    licensePlate: String = "ABC1234",
    state: UsState = UsState.NY,
    nickname: String? = null,
    createdAt: Long = 1000L,
    updatedAt: Long = 1000L
) = Vehicle(
    id = id,
    licensePlate = licensePlate,
    state = state,
    nickname = nickname,
    createdAt = createdAt,
    updatedAt = updatedAt
)
