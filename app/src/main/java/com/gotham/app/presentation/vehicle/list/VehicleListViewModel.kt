package com.gotham.app.presentation.vehicle.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotham.app.domain.model.Vehicle
import com.gotham.app.domain.usecase.vehicle.DeleteVehicleUseCase
import com.gotham.app.domain.usecase.vehicle.GetVehiclesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleListViewModel @Inject constructor(
    getVehiclesUseCase: GetVehiclesUseCase,
    private val deleteVehicleUseCase: DeleteVehicleUseCase
) : ViewModel() {

    val vehicles: StateFlow<List<Vehicle>> = getVehiclesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteVehicle(vehicleId: Long) {
        viewModelScope.launch {
            deleteVehicleUseCase(vehicleId)
        }
    }
}
