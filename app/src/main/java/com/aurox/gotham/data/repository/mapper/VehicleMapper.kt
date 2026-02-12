package com.aurox.gotham.data.repository.mapper

import com.aurox.gotham.data.local.entity.VehicleEntity
import com.aurox.gotham.domain.model.UsState
import com.aurox.gotham.domain.model.Vehicle

fun VehicleEntity.toDomain(): Vehicle? {
    val usState = UsState.fromCode(state) ?: return null

    return Vehicle(
        id = id,
        licensePlate = licensePlate,
        state = usState,
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
        nickname = nickname?.trim()?.takeIf { it.isNotEmpty() },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<VehicleEntity>.toDomainList(): List<Vehicle> {
    return mapNotNull { it.toDomain() }
}
