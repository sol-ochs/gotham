package com.gotham.app.domain.usecase.sync

import com.gotham.app.domain.model.Ticket
import com.gotham.app.domain.repository.TicketRepository
import com.gotham.app.domain.repository.VehicleRepository
import com.gotham.app.domain.util.Result
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
