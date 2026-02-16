package com.aurox.gotham.domain.repository

import com.aurox.gotham.domain.model.Ticket
import com.aurox.gotham.domain.model.Vehicle
import com.aurox.gotham.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

interface TicketRepository {
    fun getAllTickets(): Flow<List<Ticket>>
    fun getTicketsByVehicleId(vehicleId: Long): Flow<List<Ticket>>
    suspend fun getTicketBySummonsNumber(summonsNumber: String): Ticket?
    fun observeTicketBySummonsNumber(summonsNumber: String): Flow<Ticket?>
    fun getNewTickets(thresholdDate: LocalDateTime): Flow<List<Ticket>>
    fun getNewTicketCount(thresholdDate: LocalDateTime): Flow<Int>
    suspend fun markTicketAsSeen(summonsNumber: String)
    suspend fun markAllTicketsAsSeen()
    suspend fun fetchTicketsForVehicle(vehicle: Vehicle): Result<List<Ticket>>
    suspend fun syncTicketsForVehicle(vehicle: Vehicle): Result<List<Ticket>>
    suspend fun checkForNewTickets(vehicles: List<Vehicle>): Result<List<Ticket>>
    suspend fun getUnpaidReminderTicketCount(): Int
    suspend fun getUnpaidReminderTicketTotal(): Double
    suspend fun setTicketPaidOverride(summonsNumber: String, isEnabled: Boolean)
    suspend fun getDeadlineReminderCandidates(today: LocalDate): List<Ticket>
    suspend fun hasDeadlineReminderEvent(summonsNumber: String, milestoneDay: Int): Boolean
    suspend fun recordDeadlineReminderEvent(summonsNumber: String, milestoneDay: Int, sentAt: Long = System.currentTimeMillis())
}
