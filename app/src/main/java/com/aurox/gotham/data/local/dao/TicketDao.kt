package com.aurox.gotham.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aurox.gotham.data.local.entity.TicketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets ORDER BY issue_date_time DESC, summons_number DESC")
    fun getAllTickets(): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE vehicle_id = :vehicleId ORDER BY issue_date_time DESC, summons_number DESC")
    fun getTicketsByVehicleId(vehicleId: Long): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE summons_number = :summonsNumber")
    suspend fun getTicketBySummonsNumber(summonsNumber: String): TicketEntity?

    @Query("SELECT * FROM tickets WHERE summons_number = :summonsNumber")
    fun observeTicketBySummonsNumber(summonsNumber: String): Flow<TicketEntity?>

    @Query("""
        SELECT * FROM tickets
        WHERE is_new = 1
        AND amount_due > 0
        AND issue_date_time >= :thresholdDate
    """)
    fun getNewTickets(thresholdDate: String): Flow<List<TicketEntity>>

    @Query("""
        SELECT COUNT(*) FROM tickets
        WHERE is_new = 1
        AND amount_due > 0
        AND issue_date_time >= :thresholdDate
    """)
    fun getNewTicketCount(thresholdDate: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM tickets WHERE is_new = 1 AND vehicle_id = :vehicleId")
    fun getNewTicketCountByVehicle(vehicleId: Long): Flow<Int>

    @Query("UPDATE tickets SET is_new = 0 WHERE summons_number = :summonsNumber")
    suspend fun markTicketAsSeen(summonsNumber: String)

    @Query("UPDATE tickets SET is_new = 0")
    suspend fun markAllTicketsAsSeen()

    @Query("""
        UPDATE tickets
        SET is_paid_override = :isPaidOverride,
            paid_override_at = :paidOverrideAt
        WHERE summons_number = :summonsNumber
    """)
    suspend fun updatePaidOverride(
        summonsNumber: String,
        isPaidOverride: Boolean,
        paidOverrideAt: Long?
    )

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTicket(ticket: TicketEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTickets(tickets: List<TicketEntity>): List<Long>

    @Update
    suspend fun updateTicket(ticket: TicketEntity)

    @Query("DELETE FROM tickets WHERE summons_number = :summonsNumber")
    suspend fun deleteTicketBySummonsNumber(summonsNumber: String)

    @Query("DELETE FROM tickets WHERE vehicle_id = :vehicleId")
    suspend fun deleteTicketsByVehicleId(vehicleId: Long)

    @Query("DELETE FROM tickets")
    suspend fun deleteAllTickets()

    @Query("SELECT EXISTS(SELECT 1 FROM tickets WHERE summons_number = :summonsNumber)")
    suspend fun ticketExists(summonsNumber: String): Boolean

    @Query("SELECT summons_number FROM tickets")
    suspend fun getAllSummonsNumbers(): List<String>

    @Query("SELECT summons_number FROM tickets WHERE vehicle_id = :vehicleId")
    suspend fun getSummonsNumbersByVehicle(vehicleId: Long): List<String>

    @Query("""
        SELECT COUNT(*) FROM tickets
        WHERE amount_due > 0
        AND issue_date_time > :thresholdDate
        AND is_paid_override = 0
    """)
    suspend fun getUnpaidReminderTicketCount(thresholdDate: String): Int

    @Query("""
        SELECT SUM(amount_due) FROM tickets
        WHERE amount_due > 0
        AND issue_date_time > :thresholdDate
        AND is_paid_override = 0
    """)
    suspend fun getUnpaidReminderTicketTotal(thresholdDate: String): Double?

    @Query("""
        SELECT * FROM tickets
        WHERE amount_due > 0
        AND issue_date_time > :thresholdDate
        AND issue_date_time <= :upperBoundDate
        AND is_paid_override = 0
        ORDER BY issue_date_time DESC
    """)
    suspend fun getDeadlineReminderCandidates(
        thresholdDate: String,
        upperBoundDate: String
    ): List<TicketEntity>
}
