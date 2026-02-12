package com.aurox.gotham.domain.repository

import com.aurox.gotham.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getAllVehicles(): Flow<List<Vehicle>>
    suspend fun getVehicleById(vehicleId: Long): Vehicle?
    fun observeVehicleById(vehicleId: Long): Flow<Vehicle?>
    suspend fun getVehicleCount(): Int
    fun observeVehicleCount(): Flow<Int>
    suspend fun insertVehicle(vehicle: Vehicle): Long
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteVehicle(vehicle: Vehicle)
    suspend fun deleteVehicleById(vehicleId: Long)
    suspend fun canAddVehicle(): Boolean
    suspend fun existsByPlateAndState(
        plate: String,
        stateCode: String,
        excludeVehicleId: Long? = null
    ): Boolean
}
