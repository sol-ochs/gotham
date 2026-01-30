package com.aurox.gotham.domain.usecase.sync

import com.aurox.gotham.domain.model.Ticket
import com.aurox.gotham.domain.repository.TicketRepository
import com.aurox.gotham.domain.repository.VehicleRepository
import com.aurox.gotham.domain.util.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckForNewTicketsUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(): Result<List<Ticket>> {
        val vehicles = vehicleRepository.getAllVehicles().first()

        if (vehicles.isEmpty()) {
            return Result.Success(emptyList())
        }

        return ticketRepository.checkForNewTickets(vehicles)
    }
}
