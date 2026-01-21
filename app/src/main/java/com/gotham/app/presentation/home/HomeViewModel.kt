package com.gotham.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotham.app.domain.usecase.sync.RefreshTicketsUseCase
import com.gotham.app.domain.usecase.ticket.GetUnseenTicketCountUseCase
import com.gotham.app.domain.usecase.vehicle.GetVehiclesWithTicketSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getVehiclesWithTicketSummaryUseCase: GetVehiclesWithTicketSummaryUseCase,
    private val getUnseenTicketCountUseCase: GetUnseenTicketCountUseCase,
    private val refreshTicketsUseCase: RefreshTicketsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            combine(
                getVehiclesWithTicketSummaryUseCase(),
                getUnseenTicketCountUseCase()
            ) { vehicleSummaries, unseenCount ->
                val totalBalance = vehicleSummaries.sumOf { it.amountDue }
                HomeState(
                    vehicles = vehicleSummaries,
                    totalBalanceDue = totalBalance,
                    unseenTicketCount = unseenCount,
                    isLoading = false
                )
            }
            .catch { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load data"
                    )
                }
            }
            .collect { newState ->
                _state.value = newState
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            refreshTicketsUseCase()
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
