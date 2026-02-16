package com.aurox.gotham.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tickets",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicle_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["vehicle_id"]),
        Index(value = ["summons_number"], unique = true)
    ]
)
data class TicketEntity(
    @PrimaryKey
    @ColumnInfo(name = "summons_number")
    val summonsNumber: String,

    @ColumnInfo(name = "vehicle_id")
    val vehicleId: Long,

    @ColumnInfo(name = "plate")
    val plate: String,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "license_type")
    val licenseType: String?,

    @ColumnInfo(name = "issue_date_time")
    val issueDateTime: String,

    @ColumnInfo(name = "violation")
    val violation: String,

    @ColumnInfo(name = "violation_location")
    val violationLocation: String?,

    @ColumnInfo(name = "fine_amount")
    val fineAmount: Double,

    @ColumnInfo(name = "amount_due")
    val amountDue: Double,

    @ColumnInfo(name = "violation_status")
    val violationStatus: String?,

    @ColumnInfo(name = "penalty_amount")
    val penaltyAmount: Double?,

    @ColumnInfo(name = "interest_amount")
    val interestAmount: Double?,

    @ColumnInfo(name = "payment_amount")
    val paymentAmount: Double?,

    @ColumnInfo(name = "is_new")
    val isNew: Boolean = true,

    @ColumnInfo(name = "is_paid_override")
    val isPaidOverride: Boolean = false,

    @ColumnInfo(name = "paid_override_at")
    val paidOverrideAt: Long? = null,

    @ColumnInfo(name = "first_seen_at")
    val firstSeenAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_updated_at")
    val lastUpdatedAt: Long = System.currentTimeMillis()
)
