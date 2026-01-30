package com.aurox.gotham.domain.usecase.sync

import com.aurox.gotham.domain.model.Ticket
import com.aurox.gotham.domain.repository.TicketRepository
import com.aurox.gotham.domain.repository.VehicleRepository
import com.aurox.gotham.domain.util.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RefreshTicketsUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val vehicles = vehicleRepository.getAllVehicles().first()

        if (vehicles.isEmpty()) {
            return Result.Success(Unit)
        }

        return when (val result = ticketRepository.checkForNewTickets(vehicles)) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> result
            is Result.Loading -> Result.Loading
        }
    }
}
