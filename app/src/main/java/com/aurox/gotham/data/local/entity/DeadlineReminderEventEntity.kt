package com.aurox.gotham.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "deadline_reminder_events",
    primaryKeys = ["summons_number", "milestone_day"]
)
data class DeadlineReminderEventEntity(
    @ColumnInfo(name = "summons_number")
    val summonsNumber: String,

    @ColumnInfo(name = "milestone_day")
    val milestoneDay: Int,

    @ColumnInfo(name = "sent_at")
    val sentAt: Long = System.currentTimeMillis()
)
