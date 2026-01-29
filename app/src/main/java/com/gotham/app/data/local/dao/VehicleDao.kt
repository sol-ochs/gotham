package com.gotham.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gotham.app.data.local.entity.VehicleEntity
import com.gotham.app.data.local.entity.VehicleWithTickets
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY created_at DESC")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    suspend fun getVehicleById(vehicleId: Long): VehicleEntity?

    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    fun observeVehicleById(vehicleId: Long): Flow<VehicleEntity?>

    @Transaction
    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    suspend fun getVehicleWithTickets(vehicleId: Long): VehicleWithTickets?

    @Transaction
    @Query("SELECT * FROM vehicles ORDER BY created_at DESC")
    fun getAllVehiclesWithTickets(): Flow<List<VehicleWithTickets>>

    @Query("SELECT COUNT(*) FROM vehicles")
    suspend fun getVehicleCount(): Int

    @Query("SELECT COUNT(*) FROM vehicles")
    fun observeVehicleCount(): Flow<Int>

    @Query("SELECT * FROM vehicles WHERE license_plate = :plate LIMIT 1")
    suspend fun getVehicleByPlate(plate: String): VehicleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity): Long

    @Update
    suspend fun updateVehicle(vehicle: VehicleEntity)

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)

    @Query("DELETE FROM vehicles WHERE id = :vehicleId")
    suspend fun deleteVehicleById(vehicleId: Long)

    @Query("DELETE FROM vehicles")
    suspend fun deleteAllVehicles()
}
