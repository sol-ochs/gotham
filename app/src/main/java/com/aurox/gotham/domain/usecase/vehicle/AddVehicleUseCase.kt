package com.aurox.gotham.domain.usecase.vehicle

import com.aurox.gotham.domain.model.Vehicle
import com.aurox.gotham.domain.repository.VehicleRepository
import com.aurox.gotham.domain.util.NetworkError
import com.aurox.gotham.domain.util.Result
import javax.inject.Inject

class AddVehicleUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(vehicle: Vehicle): Result<Long> {
        return try {
            val normalizedPlate = vehicle.licensePlate.trim().uppercase()
            if (normalizedPlate.isBlank()) {
                return Result.Error(
                    NetworkError.Unknown(),
                    "License plate cannot be empty"
                )
            }

            val isNewVehicle = vehicle.id == 0L
            if (isNewVehicle && !vehicleRepository.canAddVehicle()) {
                return Result.Error(
                    NetworkError.Unknown(),
                    "Maximum 5 vehicles allowed"
                )
            }

            val isDuplicate = vehicleRepository.existsByPlateAndState(
                plate = normalizedPlate,
                stateCode = vehicle.state.code,
                excludeVehicleId = vehicle.id.takeIf { it > 0L }
            )
            if (isDuplicate) {
                return Result.Error(
                    NetworkError.Unknown(),
                    "Vehicle already exists for this plate and state"
                )
            }

            val vehicleId = vehicleRepository.insertVehicle(
                vehicle.copy(licensePlate = normalizedPlate)
            )
            Result.Success(vehicleId)
        } catch (e: Exception) {
            Result.Error(NetworkError.Unknown(), e.message)
        }
    }
}
