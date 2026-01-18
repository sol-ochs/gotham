package com.gotham.app.domain.repository

import com.gotham.app.domain.model.Ticket
import com.gotham.app.domain.model.Vehicle
import com.gotham.app.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    fun getAllTickets(): Flow<List<Ticket>>
    fun getTicketsByVehicleId(vehicleId: Long): Flow<List<Ticket>>
    suspend fun getTicketBySummonsNumber(summonsNumber: String): Ticket?
    fun observeTicketBySummonsNumber(summonsNumber: String): Flow<Ticket?>
    fun getNewTickets(): Flow<List<Ticket>>
    fun getNewTicketCount(): Flow<Int>
    suspend fun markTicketAsSeen(summonsNumber: String)
    suspend fun markAllTicketsAsSeen()
    suspend fun fetchTicketsForVehicle(vehicle: Vehicle): Result<List<Ticket>>
    suspend fun syncTicketsForVehicle(vehicle: Vehicle): Result<List<Ticket>>
    suspend fun checkForNewTickets(vehicles: List<Vehicle>): Result<List<Ticket>>
}
