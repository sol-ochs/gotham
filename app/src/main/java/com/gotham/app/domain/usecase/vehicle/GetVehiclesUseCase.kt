package com.gotham.app.domain.usecase.vehicle

import com.gotham.app.domain.model.Vehicle
import com.gotham.app.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVehiclesUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    operator fun invoke(): Flow<List<Vehicle>> {
        return vehicleRepository.getAllVehicles()
    }
}
