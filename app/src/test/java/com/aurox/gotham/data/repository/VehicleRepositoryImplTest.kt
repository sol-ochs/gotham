package com.aurox.gotham.data.repository

import com.aurox.gotham.data.local.dao.VehicleDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VehicleRepositoryImplTest {

    private lateinit var vehicleDao: VehicleDao
    private lateinit var repository: VehicleRepositoryImpl

    @Before
    fun setup() {
        vehicleDao = mockk()
        repository = VehicleRepositoryImpl(vehicleDao)
    }

    @Test
    fun `existsByPlateAndState normalizes plate and state`() = runTest {
        coEvery {
            vehicleDao.existsByPlateAndState("ABC1234", "NY", null)
        } returns true

        val result = repository.existsByPlateAndState(
            plate = " abc1234 ",
            stateCode = " ny ",
            excludeVehicleId = null
        )

        assertTrue(result)
        coVerify(exactly = 1) {
            vehicleDao.existsByPlateAndState("ABC1234", "NY", null)
        }
    }

    @Test
    fun `existsByPlateAndState forwards excludeVehicleId`() = runTest {
        coEvery {
            vehicleDao.existsByPlateAndState("ABC1234", "NY", 7L)
        } returns false

        repository.existsByPlateAndState(
            plate = "ABC1234",
            stateCode = "NY",
            excludeVehicleId = 7L
        )

        coVerify(exactly = 1) {
            vehicleDao.existsByPlateAndState("ABC1234", "NY", 7L)
        }
    }
}
