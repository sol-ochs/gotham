package com.aurox.gotham.domain.usecase.vehicle

import com.aurox.gotham.domain.repository.VehicleRepository
import com.aurox.gotham.domain.util.Result
import com.aurox.gotham.testutil.createVehicle
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddVehicleUseCaseTest {

    private lateinit var vehicleRepository: VehicleRepository
    private lateinit var useCase: AddVehicleUseCase

    @Before
    fun setup() {
        vehicleRepository = mockk()
        useCase = AddVehicleUseCase(vehicleRepository)
    }

    @Test
    fun `returns error when canAddVehicle is false`() = runTest {
        coEvery { vehicleRepository.canAddVehicle() } returns false

        val result = useCase(createVehicle())

        assertTrue(result is Result.Error)
        assertEquals("Maximum 5 vehicles allowed", (result as Result.Error).message)
    }

    @Test
    fun `returns error when license plate is blank`() = runTest {
        coEvery { vehicleRepository.canAddVehicle() } returns true

        val result = useCase(createVehicle(licensePlate = ""))

        assertTrue(result is Result.Error)
        assertEquals("License plate cannot be empty", (result as Result.Error).message)
    }

    @Test
    fun `returns error when license plate is whitespace only`() = runTest {
        coEvery { vehicleRepository.canAddVehicle() } returns true

        val result = useCase(createVehicle(licensePlate = "   "))

        assertTrue(result is Result.Error)
        assertEquals("License plate cannot be empty", (result as Result.Error).message)
    }

    @Test
    fun `returns success with vehicleId when valid`() = runTest {
        coEvery { vehicleRepository.canAddVehicle() } returns true
        coEvery { vehicleRepository.insertVehicle(any()) } returns 42L

        val result = useCase(createVehicle(licensePlate = "ABC1234"))

        assertTrue(result is Result.Success)
        assertEquals(42L, (result as Result.Success).data)
    }

    @Test
    fun `returns error when repository throws exception`() = runTest {
        coEvery { vehicleRepository.canAddVehicle() } returns true
        coEvery { vehicleRepository.insertVehicle(any()) } throws RuntimeException("DB error")

        val result = useCase(createVehicle(licensePlate = "ABC1234"))

        assertTrue(result is Result.Error)
        assertEquals("DB error", (result as Result.Error).message)
    }
}
