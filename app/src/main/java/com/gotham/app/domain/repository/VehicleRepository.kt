package com.gotham.app.domain.repository

import com.gotham.app.domain.model.Vehicle
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
}
