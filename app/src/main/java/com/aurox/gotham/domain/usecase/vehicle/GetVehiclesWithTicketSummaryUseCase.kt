package com.aurox.gotham.domain.usecase.vehicle

import com.aurox.gotham.domain.repository.TicketRepository
import com.aurox.gotham.domain.repository.VehicleRepository
import com.aurox.gotham.presentation.home.VehicleWithTicketSummary
import com.aurox.gotham.util.Constants.PAYABLE_TICKET_AGE_DAYS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetVehiclesWithTicketSummaryUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val ticketRepository: TicketRepository
) {
    operator fun invoke(): Flow<List<VehicleWithTicketSummary>> {
        return vehicleRepository.getAllVehicles().flatMapLatest { vehicles ->
            if (vehicles.isEmpty()) {
                flowOf(emptyList())
            } else {
                val ticketFlows = vehicles.map { vehicle ->
                    ticketRepository.getTicketsByVehicleId(vehicle.id)
                }
                combine(ticketFlows) { ticketLists ->
                    val ageThreshold = LocalDateTime.now().minusDays(PAYABLE_TICKET_AGE_DAYS)
                    vehicles.mapIndexed { index, vehicle ->
                        val tickets = ticketLists[index]
                        val payableUnpaidTickets = tickets.filter { ticket ->
                            !ticket.isPaid && ticket.issueDateTime.isAfter(ageThreshold)
                        }
                        VehicleWithTicketSummary(
                            vehicle = vehicle,
                            openTicketCount = payableUnpaidTickets.size,
                            amountDue = payableUnpaidTickets.sumOf { it.effectiveAmountDue }
                        )
                    }
                }
            }
        }
    }
}
