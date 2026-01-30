package com.aurox.gotham.domain.usecase.vehicle

import com.aurox.gotham.domain.model.Vehicle
import com.aurox.gotham.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVehiclesUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    operator fun invoke(): Flow<List<Vehicle>> {
        return vehicleRepository.getAllVehicles()
    }
}
