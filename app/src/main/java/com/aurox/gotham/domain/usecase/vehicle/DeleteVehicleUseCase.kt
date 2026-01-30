package com.aurox.gotham.domain.usecase.vehicle

import com.aurox.gotham.domain.repository.VehicleRepository
import javax.inject.Inject

class DeleteVehicleUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(vehicleId: Long) {
        vehicleRepository.deleteVehicleById(vehicleId)
    }
}
