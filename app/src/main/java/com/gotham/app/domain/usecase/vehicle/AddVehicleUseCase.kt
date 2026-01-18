package com.gotham.app.domain.usecase.vehicle

import com.gotham.app.domain.model.Vehicle
import com.gotham.app.domain.repository.VehicleRepository
import com.gotham.app.domain.util.NetworkError
import com.gotham.app.domain.util.Result
import javax.inject.Inject

class AddVehicleUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(vehicle: Vehicle): Result<Long> {
        return try {
            if (!vehicleRepository.canAddVehicle()) {
                return Result.Error(
                    NetworkError.Unknown(),
                    "Maximum 5 vehicles allowed"
                )
            }

            if (vehicle.licensePlate.isBlank()) {
                return Result.Error(
                    NetworkError.Unknown(),
                    "License plate cannot be empty"
                )
            }

            val vehicleId = vehicleRepository.insertVehicle(vehicle)
            Result.Success(vehicleId)
        } catch (e: Exception) {
            Result.Error(NetworkError.Unknown(), e.message)
        }
    }
}
