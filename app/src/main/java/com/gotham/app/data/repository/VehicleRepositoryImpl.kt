package com.gotham.app.data.repository

import com.gotham.app.data.local.dao.VehicleDao
import com.gotham.app.data.repository.mapper.toDomain
import com.gotham.app.data.repository.mapper.toDomainList
import com.gotham.app.data.repository.mapper.toEntity
import com.gotham.app.domain.model.Vehicle
import com.gotham.app.domain.repository.VehicleRepository
import com.gotham.app.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao
) : VehicleRepository {

    override fun getAllVehicles(): Flow<List<Vehicle>> {
        return vehicleDao.getAllVehicles()
            .map { it.toDomainList() }
    }

    override suspend fun getVehicleById(vehicleId: Long): Vehicle? {
        return vehicleDao.getVehicleById(vehicleId)?.toDomain()
    }

    override fun observeVehicleById(vehicleId: Long): Flow<Vehicle?> {
        return vehicleDao.observeVehicleById(vehicleId)
            .map { it?.toDomain() }
    }

    override suspend fun getVehicleCount(): Int {
        return vehicleDao.getVehicleCount()
    }

    override fun observeVehicleCount(): Flow<Int> {
        return vehicleDao.observeVehicleCount()
    }

    override suspend fun insertVehicle(vehicle: Vehicle): Long {
        return vehicleDao.insertVehicle(vehicle.toEntity())
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        vehicleDao.updateVehicle(vehicle.toEntity())
    }

    override suspend fun deleteVehicle(vehicle: Vehicle) {
        vehicleDao.deleteVehicle(vehicle.toEntity())
    }

    override suspend fun deleteVehicleById(vehicleId: Long) {
        vehicleDao.deleteVehicleById(vehicleId)
    }

    override suspend fun canAddVehicle(): Boolean {
        return getVehicleCount() < Constants.MAX_VEHICLES
    }
}
