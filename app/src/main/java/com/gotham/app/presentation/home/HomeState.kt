package com.gotham.app.presentation.home

import com.gotham.app.domain.model.Vehicle

data class VehicleWithTicketSummary(
    val vehicle: Vehicle,
    val openTicketCount: Int,
    val amountDue: Double
)

data class HomeState(
    val vehicles: List<VehicleWithTicketSummary> = emptyList(),
    val totalBalanceDue: Double = 0.0,
    val unseenTicketCount: Int = 0,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
