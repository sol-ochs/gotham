package com.aurox.gotham.domain.usecase.vehicle

import com.aurox.gotham.domain.repository.VehicleRepository
import com.aurox.gotham.domain.util.Result
import com.aurox.gotham.testutil.createVehicle
import io.mockk.coEvery
import io.mockk.coVerify
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
        coVerify(exactly = 0) { vehicleRepository.existsByPlateAndState(any(), any(), any()) }
        coVerify(exactly = 0) { vehicleRepository.insertVehicle(any()) }
    }

    @Test
    fun `returns error when license plate is blank`() = runTest {
        val result = useCase(createVehicle(licensePlate = ""))

        assertTrue(result is Result.Error)
        assertEquals("License plate cannot be empty", (result as Result.Error).message)
        coVerify(exactly = 0) { vehicleRepository.canAddVehicle() }
        coVerify(exactly = 0) { vehicleRepository.existsByPlateAndState(any(), any(), any()) }
        coVerify(exactly = 0) { vehicleRepository.insertVehicle(any()) }
    }

    @Test
    fun `returns error when license plate is whitespace only`() = runTest {
        val result = useCase(createVehicle(licensePlate = "   "))

        assertTrue(result is Result.Error)
        assertEquals("License plate cannot be empty", (result as Result.Error).message)
        coVerify(exactly = 0) { vehicleRepository.canAddVehicle() }
        coVerify(exactly = 0) { vehicleRepository.existsByPlateAndState(any(), any(), any()) }
        coVerify(exactly = 0) { vehicleRepository.insertVehicle(any()) }
    }

    @Test
    fun `returns error when duplicate vehicle exists for new vehicle`() = runTest {
        coEvery { vehicleRepository.canAddVehicle() } returns true
        coEvery {
            vehicleRepository.existsByPlateAndState("ABC1234", "NY", null)
        } returns true

        val result = useCase(createVehicle(licensePlate = " abc1234 "))

        assertTrue(result is Result.Error)
        assertEquals("Vehicle already exists for this plate and state", (result as Result.Error).message)
        coVerify(exactly = 0) { vehicleRepository.insertVehicle(any()) }
    }

    @Test
    fun `returns error when duplicate vehicle exists for different existing vehicle on edit`() = runTest {
        coEvery {
            vehicleRepository.existsByPlateAndState("ABC1234", "NY", 10L)
        } returns true

        val result = useCase(createVehicle(id = 10L, licensePlate = "ABC1234"))

        assertTrue(result is Result.Error)
        assertEquals("Vehicle already exists for this plate and state", (result as Result.Error).message)
        coVerify(exactly = 0) { vehicleRepository.canAddVehicle() }
        coVerify(exactly = 0) { vehicleRepository.insertVehicle(any()) }
    }

    @Test
    fun `allows edit when max vehicles reached`() = runTest {
        coEvery {
            vehicleRepository.existsByPlateAndState("ABC1234", "NY", 42L)
        } returns false
        coEvery { vehicleRepository.insertVehicle(any()) } returns 42L

        val result = useCase(createVehicle(id = 42L, licensePlate = "ABC1234"))

        assertTrue(result is Result.Success)
        assertEquals(42L, (result as Result.Success).data)
        coVerify(exactly = 0) { vehicleRepository.canAddVehicle() }
    }

    @Test
    fun `normalizes plate before insert`() = runTest {
        coEvery { vehicleRepository.canAddVehicle() } returns true
        coEvery {
            vehicleRepository.existsByPlateAndState("ABC1234", "NY", null)
        } returns false
        coEvery { vehicleRepository.insertVehicle(any()) } returns 42L

        val result = useCase(createVehicle(licensePlate = " abc1234 "))

        assertTrue(result is Result.Success)
        coVerify {
            vehicleRepository.insertVehicle(match { it.licensePlate == "ABC1234" })
        }
    }

    @Test
    fun `returns success with vehicleId when valid`() = runTest {
        coEvery { vehicleRepository.canAddVehicle() } returns true
        coEvery {
            vehicleRepository.existsByPlateAndState("ABC1234", "NY", null)
        } returns false
        coEvery { vehicleRepository.insertVehicle(any()) } returns 42L

        val result = useCase(createVehicle(licensePlate = "ABC1234"))

        assertTrue(result is Result.Success)
        assertEquals(42L, (result as Result.Success).data)
    }

    @Test
    fun `returns error when repository throws exception`() = runTest {
        coEvery { vehicleRepository.canAddVehicle() } returns true
        coEvery {
            vehicleRepository.existsByPlateAndState("ABC1234", "NY", null)
        } returns false
        coEvery { vehicleRepository.insertVehicle(any()) } throws RuntimeException("DB error")

        val result = useCase(createVehicle(licensePlate = "ABC1234"))

        assertTrue(result is Result.Error)
        assertEquals("DB error", (result as Result.Error).message)
    }
}
