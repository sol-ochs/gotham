package com.gotham.app.presentation.ticket.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotham.app.domain.model.Ticket
import com.gotham.app.domain.model.ViolationType
import com.gotham.app.domain.usecase.sync.RefreshTicketsUseCase
import com.gotham.app.domain.usecase.ticket.GetTicketsUseCase
import com.gotham.app.domain.usecase.ticket.GetUnseenTicketCountUseCase
import com.gotham.app.domain.util.Result
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
    private val getTicketsUseCase: GetTicketsUseCase,
    private val getUnseenTicketCountUseCase: GetUnseenTicketCountUseCase,
    private val refreshTicketsUseCase: RefreshTicketsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TicketListState())
    val state: StateFlow<TicketListState> = _state.asStateFlow()

    private var allTickets: List<Ticket> = emptyList()

    init {
        observeTickets()
        refresh()
    }

    private fun observeTickets() {
        viewModelScope.launch {
            combine(
                getTicketsUseCase(),
                getUnseenTicketCountUseCase()
            ) { tickets, unseenCount ->
                allTickets = tickets
                _state.update {
                    it.copy(
                        tickets = applyFilters(
                            tickets,
                            it.selectedTypeFilter,
                            it.selectedStatusFilter,
                            it.showOlderUnpaid
                        ),
                        unseenCount = unseenCount,
                        isLoading = false,
                        totalAmountOwed = calculateTotalAmountOwed(tickets, it.showOlderUnpaid),
                        hiddenOlderUnpaidCount = countHiddenOlderUnpaid(tickets, it.selectedTypeFilter)
                    )
                }
            }.collect {}
        }
    }

    fun onTypeFilterChanged(filter: TicketTypeFilter) {
        _state.update {
            it.copy(
                selectedTypeFilter = filter,
                tickets = applyFilters(allTickets, filter, it.selectedStatusFilter, it.showOlderUnpaid),
                hiddenOlderUnpaidCount = countHiddenOlderUnpaid(allTickets, filter)
            )
        }
    }

    fun onStatusFilterChanged(filter: TicketStatusFilter) {
        _state.update {
            it.copy(
                selectedStatusFilter = filter,
                showOlderUnpaid = false,
                tickets = applyFilters(allTickets, it.selectedTypeFilter, filter, false)
            )
        }
    }

    fun onToggleShowOlderUnpaid() {
        val newValue = !_state.value.showOlderUnpaid
        _state.update {
            it.copy(
                showOlderUnpaid = newValue,
                tickets = applyFilters(allTickets, it.selectedTypeFilter, it.selectedStatusFilter, newValue),
                totalAmountOwed = calculateTotalAmountOwed(allTickets, newValue)
            )
        }
    }

    private fun applyFilters(
        tickets: List<Ticket>,
        typeFilter: TicketTypeFilter,
        statusFilter: TicketStatusFilter,
        showOlderUnpaid: Boolean
    ): List<Ticket> {
        val threshold = LocalDateTime.now().minusDays(OLDER_UNPAID_THRESHOLD_DAYS)

        return tickets
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

    private fun countHiddenOlderUnpaid(tickets: List<Ticket>, typeFilter: TicketTypeFilter): Int {
        val threshold = LocalDateTime.now().minusDays(OLDER_UNPAID_THRESHOLD_DAYS)

        return tickets
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

    private fun calculateTotalAmountOwed(tickets: List<Ticket>, showOlderUnpaid: Boolean): Double {
        val threshold = LocalDateTime.now().minusDays(OLDER_UNPAID_THRESHOLD_DAYS)
        return tickets
            .filter { !it.isPaid }
            .filter { ticket ->
                if (showOlderUnpaid) true else ticket.issueDateTime.isAfter(threshold)
            }
            .sumOf { it.amountDue }
    }

    companion object {
        private const val OLDER_UNPAID_THRESHOLD_DAYS = 90L
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
