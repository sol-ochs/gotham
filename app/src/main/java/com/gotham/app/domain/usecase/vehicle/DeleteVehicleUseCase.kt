package com.gotham.app.domain.usecase.vehicle

import com.gotham.app.domain.repository.VehicleRepository
import javax.inject.Inject

class DeleteVehicleUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(vehicleId: Long) {
        vehicleRepository.deleteVehicleById(vehicleId)
    }
}
