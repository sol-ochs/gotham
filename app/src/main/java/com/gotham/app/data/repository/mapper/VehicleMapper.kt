package com.gotham.app.data.repository.mapper

import com.gotham.app.data.local.entity.VehicleEntity
import com.gotham.app.domain.model.UsState
import com.gotham.app.domain.model.Vehicle
import com.gotham.app.domain.model.VehicleType

fun VehicleEntity.toDomain(): Vehicle? {
    val usState = UsState.fromCode(state) ?: return null
    val vehicleType = VehicleType.fromCode(this.vehicleType)

    return Vehicle(
        id = id,
        licensePlate = licensePlate,
        state = usState,
        vehicleType = vehicleType,
        nickname = nickname,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Vehicle.toEntity(): VehicleEntity {
    return VehicleEntity(
        id = id,
        licensePlate = licensePlate.uppercase().trim(),
        state = state.code,
        vehicleType = vehicleType.code,
        nickname = nickname?.trim()?.takeIf { it.isNotEmpty() },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<VehicleEntity>.toDomainList(): List<Vehicle> {
    return mapNotNull { it.toDomain() }
}
