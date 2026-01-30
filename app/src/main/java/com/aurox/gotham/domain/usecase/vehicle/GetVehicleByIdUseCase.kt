package com.aurox.gotham.domain.usecase.vehicle

import com.aurox.gotham.domain.model.Vehicle
import com.aurox.gotham.domain.repository.VehicleRepository
import javax.inject.Inject

class GetVehicleByIdUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(vehicleId: Long): Vehicle? {
        return vehicleRepository.getVehicleById(vehicleId)
    }
}
