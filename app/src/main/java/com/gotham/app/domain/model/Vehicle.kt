package com.gotham.app.domain.model

data class Vehicle(
    val id: Long = 0,
    val licensePlate: String,
    val state: UsState,
    val vehicleType: VehicleType,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val displayName: String
        get() = "$licensePlate (${state.code})"
}
