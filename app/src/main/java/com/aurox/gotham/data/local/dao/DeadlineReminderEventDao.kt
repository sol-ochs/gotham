package com.aurox.gotham.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aurox.gotham.data.local.entity.DeadlineReminderEventEntity

@Dao
interface DeadlineReminderEventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(event: DeadlineReminderEventEntity): Long

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM deadline_reminder_events
            WHERE summons_number = :summonsNumber
            AND milestone_day = :milestoneDay
        )
    """)
    suspend fun hasEvent(summonsNumber: String, milestoneDay: Int): Boolean
}
