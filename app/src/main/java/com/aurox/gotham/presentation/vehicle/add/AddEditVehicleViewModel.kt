package com.aurox.gotham.presentation.vehicle.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurox.gotham.data.worker.WorkManagerScheduler
import com.aurox.gotham.domain.model.UsState
import com.aurox.gotham.domain.model.Vehicle
import com.aurox.gotham.domain.usecase.sync.CheckForNewTicketsUseCase
import com.aurox.gotham.domain.usecase.vehicle.AddVehicleUseCase
import com.aurox.gotham.domain.usecase.vehicle.DeleteVehicleUseCase
import com.aurox.gotham.domain.usecase.vehicle.GetVehicleByIdUseCase
import com.aurox.gotham.domain.usecase.vehicle.GetVehiclesUseCase
import com.aurox.gotham.domain.util.Result
import com.aurox.gotham.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditVehicleViewModel @Inject constructor(
    private val addVehicleUseCase: AddVehicleUseCase,
    private val deleteVehicleUseCase: DeleteVehicleUseCase,
    private val getVehicleByIdUseCase: GetVehicleByIdUseCase,
    private val getVehiclesUseCase: GetVehiclesUseCase,
    private val checkForNewTicketsUseCase: CheckForNewTicketsUseCase,
    private val workManagerScheduler: WorkManagerScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditVehicleState())
    val state: StateFlow<AddEditVehicleState> = _state.asStateFlow()

    private val _isFirstVehicle = MutableStateFlow(false)
    val isFirstVehicle: StateFlow<Boolean> = _isFirstVehicle.asStateFlow()

    init {
        val vehicleIdArg = savedStateHandle.get<String>(Screen.AddEditVehicle.ARG_VEHICLE_ID)
        if (vehicleIdArg != null && vehicleIdArg != "new") {
            loadVehicle(vehicleIdArg.toLongOrNull())
        }

        viewModelScope.launch {
            val vehicles = getVehiclesUseCase().first()
            _isFirstVehicle.value = vehicles.isEmpty()
        }
    }

    private fun loadVehicle(vehicleId: Long?) {
        if (vehicleId == null) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val vehicle = getVehicleByIdUseCase(vehicleId)
            if (vehicle != null) {
                _state.update {
                    it.copy(
                        vehicleId = vehicle.id,
                        licensePlate = vehicle.licensePlate,
                        nickname = vehicle.nickname ?: "",
                        selectedState = vehicle.state,
                        isLoading = false
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Vehicle not found"
                    )
                }
            }
        }
    }

    fun onLicensePlateChange(plate: String) {
        _state.update { it.copy(licensePlate = plate.uppercase().trim()) }
    }

    fun onNicknameChange(nickname: String) {
        _state.update { it.copy(nickname = nickname) }
    }

    fun onStateSelected(state: UsState) {
        _state.update { it.copy(selectedState = state) }
    }

    fun saveVehicle() {
        viewModelScope.launch {
            val currentState = _state.value

            if (currentState.licensePlate.isBlank()) {
                _state.update { it.copy(error = "License plate cannot be empty") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }

            val vehicle = Vehicle(
                id = currentState.vehicleId ?: 0,
                licensePlate = currentState.licensePlate,
                state = currentState.selectedState,
                nickname = currentState.nickname.takeIf { it.isNotBlank() }
            )

            when (val result = addVehicleUseCase(vehicle)) {
                is Result.Success -> {
                    if (_isFirstVehicle.value) {
                        workManagerScheduler.schedulePeriodicTicketCheck()
                        workManagerScheduler.scheduleUnpaidReminder()
                        workManagerScheduler.scheduleDeadlineReminder()
                    }

                    // Immediately fetch tickets for the new vehicle
                    checkForNewTicketsUseCase()

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSaved = true
                        )
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
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

    fun deleteVehicle() {
        val vehicleId = _state.value.vehicleId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                deleteVehicleUseCase(vehicleId)
                _state.update { it.copy(isLoading = false, isDeleted = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to delete vehicle"
                    )
                }
            }
        }
    }
}
