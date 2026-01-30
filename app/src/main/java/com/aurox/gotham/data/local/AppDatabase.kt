package com.aurox.gotham.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aurox.gotham.data.local.dao.TicketDao
import com.aurox.gotham.data.local.dao.VehicleDao
import com.aurox.gotham.data.local.entity.TicketEntity
import com.aurox.gotham.data.local.entity.VehicleEntity

@Database(
    entities = [VehicleEntity::class, TicketEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun ticketDao(): TicketDao
}
