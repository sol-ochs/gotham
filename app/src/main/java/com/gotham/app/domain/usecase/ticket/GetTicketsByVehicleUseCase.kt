package com.gotham.app.domain.usecase.ticket

import com.gotham.app.domain.model.Ticket
import com.gotham.app.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTicketsByVehicleUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    operator fun invoke(vehicleId: Long): Flow<List<Ticket>> {
        return ticketRepository.getTicketsByVehicleId(vehicleId)
    }
}
