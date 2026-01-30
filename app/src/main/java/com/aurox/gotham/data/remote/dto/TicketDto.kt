package com.aurox.gotham.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TicketDto(
    @Json(name = "plate")
    val plate: String?,

    @Json(name = "state")
    val state: String?,

    @Json(name = "license_type")
    val licenseType: String?,

    @Json(name = "summons_number")
    val summonsNumber: String,

    @Json(name = "issue_date")
    val issueDate: String?,

    @Json(name = "violation_time")
    val violationTime: String?,

    @Json(name = "violation")
    val violation: String?,

    @Json(name = "violation_location")
    val violationLocation: String?,

    @Json(name = "fine_amount")
    val fineAmount: String?,

    @Json(name = "amount_due")
    val amountDue: String?,

    @Json(name = "violation_status")
    val violationStatus: String?,

    @Json(name = "penalty_amount")
    val penaltyAmount: String?,

    @Json(name = "interest_amount")
    val interestAmount: String?,

    @Json(name = "payment_amount")
    val paymentAmount: String?
)
