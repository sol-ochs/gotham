package com.aurox.gotham.domain.usecase.ticket

import com.aurox.gotham.domain.model.Ticket
import com.aurox.gotham.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTicketsByVehicleUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    operator fun invoke(vehicleId: Long): Flow<List<Ticket>> {
        return ticketRepository.getTicketsByVehicleId(vehicleId)
    }
}
