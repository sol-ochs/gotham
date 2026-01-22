package com.gotham.app.data.repository

import com.gotham.app.data.local.dao.TicketDao
import com.gotham.app.data.remote.NycOpenDataApi
import com.gotham.app.data.repository.mapper.toDomain
import com.gotham.app.data.repository.mapper.toDomainList
import com.gotham.app.data.repository.mapper.toEntityList
import com.gotham.app.domain.model.Ticket
import com.gotham.app.domain.model.Vehicle
import com.gotham.app.domain.repository.TicketRepository
import com.gotham.app.domain.util.NetworkError
import com.gotham.app.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketRepositoryImpl @Inject constructor(
    private val ticketDao: TicketDao,
    private val api: NycOpenDataApi
) : TicketRepository {

    override fun getAllTickets(): Flow<List<Ticket>> {
        return ticketDao.getAllTickets()
            .map { it.toDomainList() }
    }

    override fun getTicketsByVehicleId(vehicleId: Long): Flow<List<Ticket>> {
        return ticketDao.getTicketsByVehicleId(vehicleId)
            .map { it.toDomainList() }
    }

    override suspend fun getTicketBySummonsNumber(summonsNumber: String): Ticket? {
        return ticketDao.getTicketBySummonsNumber(summonsNumber)?.toDomain()
    }

    override fun observeTicketBySummonsNumber(summonsNumber: String): Flow<Ticket?> {
        return ticketDao.observeTicketBySummonsNumber(summonsNumber)
            .map { it?.toDomain() }
    }

    override fun getNewTickets(thresholdDate: LocalDateTime): Flow<List<Ticket>> {
        return ticketDao.getNewTickets(thresholdDate.toString())
            .map { it.toDomainList() }
    }

    override fun getNewTicketCount(thresholdDate: LocalDateTime): Flow<Int> {
        return ticketDao.getNewTicketCount(thresholdDate.toString())
    }

    override suspend fun markTicketAsSeen(summonsNumber: String) {
        ticketDao.markTicketAsSeen(summonsNumber)
    }

    override suspend fun markAllTicketsAsSeen() {
        ticketDao.markAllTicketsAsSeen()
    }

    override suspend fun fetchTicketsForVehicle(vehicle: Vehicle): Result<List<Ticket>> {
        return try {
            val whereClause = NycOpenDataApi.buildWhereClause(
                vehicle.licensePlate,
                vehicle.state.code
            )

            val ticketDtos = api.getTickets(whereClause = whereClause)
            val tickets = ticketDtos.toEntityList(vehicle.id).map { it.toDomain() }

            Result.Success(tickets)
        } catch (e: IOException) {
            Result.Error(NetworkError.NoInternet, e.message)
        } catch (e: SocketTimeoutException) {
            Result.Error(NetworkError.Timeout, e.message)
        } catch (e: HttpException) {
            val error = when (e.code()) {
                429 -> NetworkError.RateLimit
                401, 403 -> NetworkError.Unauthorized
                404 -> NetworkError.NotFound
                in 500..599 -> NetworkError.ServerError
                else -> NetworkError.Unknown(e.code())
            }
            Result.Error(error, e.message())
        } catch (e: Exception) {
            Result.Error(NetworkError.Unknown(), e.message)
        }
    }

    override suspend fun syncTicketsForVehicle(vehicle: Vehicle): Result<List<Ticket>> {
        return try {
            val whereClause = NycOpenDataApi.buildWhereClause(
                vehicle.licensePlate,
                vehicle.state.code
            )

            val ticketDtos = api.getTickets(whereClause = whereClause)

            val existingSummonsNumbers = ticketDao.getSummonsNumbersByVehicle(vehicle.id).toSet()

            val ticketEntities = ticketDtos.toEntityList(vehicle.id)

            val newTickets = mutableListOf<Ticket>()

            ticketEntities.forEach { ticketEntity ->
                if (ticketEntity.summonsNumber !in existingSummonsNumbers) {
                    val rowId = ticketDao.insertTicket(ticketEntity)
                    if (rowId > 0) {
                        newTickets.add(ticketEntity.toDomain())
                    }
                } else {
                    val existingTicket = ticketDao.getTicketBySummonsNumber(ticketEntity.summonsNumber)
                    if (existingTicket != null) {
                        val updatedTicket = ticketEntity.copy(
                            isNew = existingTicket.isNew,
                            firstSeenAt = existingTicket.firstSeenAt
                        )
                        ticketDao.updateTicket(updatedTicket)
                    }
                }
            }

            Result.Success(newTickets)
        } catch (e: IOException) {
            Result.Error(NetworkError.NoInternet, e.message)
        } catch (e: SocketTimeoutException) {
            Result.Error(NetworkError.Timeout, e.message)
        } catch (e: HttpException) {
            val error = when (e.code()) {
                429 -> NetworkError.RateLimit
                401, 403 -> NetworkError.Unauthorized
                404 -> NetworkError.NotFound
                in 500..599 -> NetworkError.ServerError
                else -> NetworkError.Unknown(e.code())
            }
            Result.Error(error, e.message())
        } catch (e: Exception) {
            Result.Error(NetworkError.Unknown(), e.message)
        }
    }

    override suspend fun checkForNewTickets(vehicles: List<Vehicle>): Result<List<Ticket>> {
        return try {
            val allNewTickets = mutableListOf<Ticket>()

            vehicles.forEach { vehicle ->
                when (val result = syncTicketsForVehicle(vehicle)) {
                    is Result.Success -> allNewTickets.addAll(result.data)
                    is Result.Error -> {
                        if (result.error is NetworkError.RateLimit) {
                            return result
                        }
                    }
                    is Result.Loading -> { }
                }
            }

            Result.Success(allNewTickets)
        } catch (e: Exception) {
            Result.Error(NetworkError.Unknown(), e.message)
        }
    }
}
