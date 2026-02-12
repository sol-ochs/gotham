package com.aurox.gotham.presentation.vehicle.add

import com.aurox.gotham.domain.model.UsState

data class AddEditVehicleState(
    val vehicleId: Long? = null,
    val licensePlate: String = "",
    val nickname: String = "",
    val selectedState: UsState = UsState.NY,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false
)
