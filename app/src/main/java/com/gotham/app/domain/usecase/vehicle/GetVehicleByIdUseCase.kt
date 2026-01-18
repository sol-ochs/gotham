package com.gotham.app.domain.usecase.vehicle

import com.gotham.app.domain.model.Vehicle
import com.gotham.app.domain.repository.VehicleRepository
import javax.inject.Inject

class GetVehicleByIdUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(vehicleId: Long): Vehicle? {
        return vehicleRepository.getVehicleById(vehicleId)
    }
}
