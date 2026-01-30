package com.aurox.gotham.presentation.ticket.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurox.gotham.domain.repository.TicketRepository
import com.aurox.gotham.domain.usecase.ticket.MarkTicketAsSeenUseCase
import com.aurox.gotham.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketDetailViewModel @Inject constructor(
    private val ticketRepository: TicketRepository,
    private val markTicketAsSeenUseCase: MarkTicketAsSeenUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(TicketDetailState())
    val state: StateFlow<TicketDetailState> = _state.asStateFlow()

    init {
        val summonsNumber = savedStateHandle.get<String>(Screen.TicketDetail.ARG_SUMMONS_NUMBER)
        if (summonsNumber != null) {
            loadTicket(summonsNumber)
            markAsSeen(summonsNumber)
        }
    }

    private fun loadTicket(summonsNumber: String) {
        viewModelScope.launch {
            ticketRepository.observeTicketBySummonsNumber(summonsNumber).collect { ticket ->
                _state.update {
                    it.copy(
                        ticket = ticket,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun markAsSeen(summonsNumber: String) {
        viewModelScope.launch {
            markTicketAsSeenUseCase(summonsNumber)
        }
    }
}
