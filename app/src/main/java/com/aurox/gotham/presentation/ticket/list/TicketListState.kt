package com.aurox.gotham.presentation.ticket.list

import com.aurox.gotham.domain.model.Ticket
import com.aurox.gotham.domain.model.Vehicle

enum class TicketTypeFilter(val displayName: String) {
    ALL("All"),
    PARKING("Parking"),
    CAMERA("Camera")
}

enum class TicketStatusFilter(val displayName: String) {
    ALL("All"),
    PAID("Paid"),
    UNPAID("Unpaid")
}

data class TicketListState(
    val tickets: List<Ticket> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val unseenCount: Int = 0,
    val selectedTypeFilter: TicketTypeFilter = TicketTypeFilter.ALL,
    val selectedStatusFilter: TicketStatusFilter = TicketStatusFilter.ALL,
    val selectedVehicleId: Long? = null,
    val availableVehicles: List<Vehicle> = emptyList(),
    val showOlderUnpaid: Boolean = false,
    val hiddenOlderUnpaidCount: Int = 0,
    val totalAmountOwed: Double = 0.0
)
