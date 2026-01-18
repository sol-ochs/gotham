package com.gotham.app.presentation.vehicle.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotham.app.data.worker.WorkManagerScheduler
import com.gotham.app.domain.model.UsState
import com.gotham.app.domain.model.Vehicle
import com.gotham.app.domain.model.VehicleType
import com.gotham.app.domain.usecase.sync.CheckForNewTicketsUseCase
import com.gotham.app.domain.usecase.vehicle.AddVehicleUseCase
import com.gotham.app.domain.usecase.vehicle.GetVehicleByIdUseCase
import com.gotham.app.domain.usecase.vehicle.GetVehiclesUseCase
import com.gotham.app.domain.util.Result
import com.gotham.app.presentation.navigation.Screen
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
                        selectedState = vehicle.state,
                        selectedVehicleType = vehicle.vehicleType,
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

    fun onStateSelected(state: UsState) {
        _state.update { it.copy(selectedState = state) }
    }

    fun onVehicleTypeSelected(type: VehicleType) {
        _state.update { it.copy(selectedVehicleType = type) }
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
                vehicleType = currentState.selectedVehicleType
            )

            when (val result = addVehicleUseCase(vehicle)) {
                is Result.Success -> {
                    if (_isFirstVehicle.value) {
                        workManagerScheduler.schedulePeriodicTicketCheck()
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
}
