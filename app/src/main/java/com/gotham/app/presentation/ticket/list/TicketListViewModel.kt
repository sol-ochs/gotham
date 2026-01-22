package com.gotham.app.presentation.ticket.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotham.app.domain.model.Ticket
import com.gotham.app.domain.model.Vehicle
import com.gotham.app.domain.model.ViolationType
import com.gotham.app.domain.usecase.sync.RefreshTicketsUseCase
import com.gotham.app.domain.usecase.ticket.GetTicketsUseCase
import com.gotham.app.domain.usecase.ticket.GetUnseenTicketCountUseCase
import com.gotham.app.domain.usecase.ticket.MarkAllTicketsAsSeenUseCase
import com.gotham.app.domain.usecase.vehicle.GetVehiclesUseCase
import com.gotham.app.domain.util.Result
import com.gotham.app.presentation.navigation.Screen
import com.gotham.app.util.Constants.PAYABLE_TICKET_AGE_DAYS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TicketListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTicketsUseCase: GetTicketsUseCase,
    private val getUnseenTicketCountUseCase: GetUnseenTicketCountUseCase,
    private val markAllTicketsAsSeenUseCase: MarkAllTicketsAsSeenUseCase,
    private val refreshTicketsUseCase: RefreshTicketsUseCase,
    private val getVehiclesUseCase: GetVehiclesUseCase
) : ViewModel() {

    private val initialVehicleId: Long? = savedStateHandle.get<String>(Screen.TicketList.ARG_VEHICLE_ID)?.toLongOrNull()
    private val initialStatusFilter: TicketStatusFilter = savedStateHandle.get<String>(Screen.TicketList.ARG_STATUS_FILTER)
        ?.let { filterName -> TicketStatusFilter.entries.find { it.name == filterName } }
        ?: TicketStatusFilter.ALL

    private val _state = MutableStateFlow(
        TicketListState(
            selectedVehicleId = initialVehicleId,
            selectedStatusFilter = initialStatusFilter
        )
    )
    val state: StateFlow<TicketListState> = _state.asStateFlow()

    private var allTickets: List<Ticket> = emptyList()
    private var allVehicles: List<Vehicle> = emptyList()
    private var hasMarkedAsSeen = false

    init {
        observeVehicles()
        observeTickets()
        refresh()
    }

    private fun observeVehicles() {
        viewModelScope.launch {
            getVehiclesUseCase().collect { vehicles ->
                allVehicles = vehicles
                _state.update { it.copy(availableVehicles = vehicles) }
            }
        }
    }

    private fun observeTickets() {
        viewModelScope.launch {
            combine(
                getTicketsUseCase(),
                getUnseenTicketCountUseCase()
            ) { tickets, unseenCount ->
                Pair(tickets, unseenCount)
            }.collect { (tickets, unseenCount) ->
                allTickets = tickets
                _state.update {
                    it.copy(
                        tickets = applyFilters(
                            tickets,
                            it.selectedTypeFilter,
                            it.selectedStatusFilter,
                            it.selectedVehicleId,
                            it.showOlderUnpaid
                        ),
                        unseenCount = unseenCount,
                        isLoading = false,
                        totalAmountOwed = calculateTotalAmountOwed(tickets, it.selectedVehicleId, it.showOlderUnpaid),
                        hiddenOlderUnpaidCount = countHiddenOlderUnpaid(tickets, it.selectedTypeFilter, it.selectedVehicleId)
                    )
                }
                if (!hasMarkedAsSeen && tickets.isNotEmpty()) {
                    hasMarkedAsSeen = true
                    markAllTicketsAsSeenUseCase()
                }
            }
        }
    }

    fun onTypeFilterChanged(filter: TicketTypeFilter) {
        _state.update {
            it.copy(
                selectedTypeFilter = filter,
                tickets = applyFilters(allTickets, filter, it.selectedStatusFilter, it.selectedVehicleId, it.showOlderUnpaid),
                hiddenOlderUnpaidCount = countHiddenOlderUnpaid(allTickets, filter, it.selectedVehicleId)
            )
        }
    }

    fun onStatusFilterChanged(filter: TicketStatusFilter) {
        _state.update {
            it.copy(
                selectedStatusFilter = filter,
                showOlderUnpaid = false,
                tickets = applyFilters(allTickets, it.selectedTypeFilter, filter, it.selectedVehicleId, false)
            )
        }
    }

    fun onVehicleFilterChanged(vehicleId: Long?) {
        _state.update {
            it.copy(
                selectedVehicleId = vehicleId,
                tickets = applyFilters(allTickets, it.selectedTypeFilter, it.selectedStatusFilter, vehicleId, it.showOlderUnpaid),
                totalAmountOwed = calculateTotalAmountOwed(allTickets, vehicleId, it.showOlderUnpaid),
                hiddenOlderUnpaidCount = countHiddenOlderUnpaid(allTickets, it.selectedTypeFilter, vehicleId)
            )
        }
    }

    fun onToggleShowOlderUnpaid() {
        val newValue = !_state.value.showOlderUnpaid
        _state.update {
            it.copy(
                showOlderUnpaid = newValue,
                tickets = applyFilters(allTickets, it.selectedTypeFilter, it.selectedStatusFilter, it.selectedVehicleId, newValue),
                totalAmountOwed = calculateTotalAmountOwed(allTickets, it.selectedVehicleId, newValue)
            )
        }
    }

    private fun applyFilters(
        tickets: List<Ticket>,
        typeFilter: TicketTypeFilter,
        statusFilter: TicketStatusFilter,
        vehicleId: Long?,
        showOlderUnpaid: Boolean
    ): List<Ticket> {
        val threshold = LocalDateTime.now().minusDays(PAYABLE_TICKET_AGE_DAYS)
        val vehiclePlate = vehicleId?.let { id -> allVehicles.find { it.id == id }?.licensePlate }

        return tickets
            .filter { ticket ->
                vehiclePlate == null || ticket.plate.equals(vehiclePlate, ignoreCase = true)
            }
            .filter { ticket ->
                when (typeFilter) {
                    TicketTypeFilter.ALL -> true
                    TicketTypeFilter.CAMERA -> ViolationType.fromCode(ticket.violation).isCamera()
                    TicketTypeFilter.PARKING -> !ViolationType.fromCode(ticket.violation).isCamera()
                }
            }
            .filter { ticket ->
                when (statusFilter) {
                    TicketStatusFilter.ALL -> true
                    TicketStatusFilter.PAID -> ticket.isPaid
                    TicketStatusFilter.UNPAID -> !ticket.isPaid
                }
            }
            .filter { ticket ->
                if (statusFilter == TicketStatusFilter.UNPAID && !showOlderUnpaid) {
                    ticket.issueDateTime.isAfter(threshold)
                } else {
                    true
                }
            }
    }

    private fun countHiddenOlderUnpaid(tickets: List<Ticket>, typeFilter: TicketTypeFilter, vehicleId: Long?): Int {
        val threshold = LocalDateTime.now().minusDays(PAYABLE_TICKET_AGE_DAYS)
        val vehiclePlate = vehicleId?.let { id -> allVehicles.find { it.id == id }?.licensePlate }

        return tickets
            .filter { ticket ->
                vehiclePlate == null || ticket.plate.equals(vehiclePlate, ignoreCase = true)
            }
            .filter { !it.isPaid }
            .filter { it.issueDateTime.isBefore(threshold) }
            .filter { ticket ->
                when (typeFilter) {
                    TicketTypeFilter.ALL -> true
                    TicketTypeFilter.CAMERA -> ViolationType.fromCode(ticket.violation).isCamera()
                    TicketTypeFilter.PARKING -> !ViolationType.fromCode(ticket.violation).isCamera()
                }
            }
            .count()
    }

    private fun calculateTotalAmountOwed(tickets: List<Ticket>, vehicleId: Long?, showOlderUnpaid: Boolean): Double {
        val threshold = LocalDateTime.now().minusDays(PAYABLE_TICKET_AGE_DAYS)
        val vehiclePlate = vehicleId?.let { id -> allVehicles.find { it.id == id }?.licensePlate }

        return tickets
            .filter { ticket ->
                vehiclePlate == null || ticket.plate.equals(vehiclePlate, ignoreCase = true)
            }
            .filter { !it.isPaid }
            .filter { ticket ->
                if (showOlderUnpaid) true else ticket.issueDateTime.isAfter(threshold)
            }
            .sumOf { it.amountDue }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }

            when (val result = refreshTicketsUseCase()) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            error = result.message ?: result.error.toUserMessage()
                        )
                    }
                }
                is Result.Loading -> { }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
