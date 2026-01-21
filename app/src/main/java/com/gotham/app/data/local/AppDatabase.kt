package com.gotham.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gotham.app.data.local.dao.TicketDao
import com.gotham.app.data.local.dao.VehicleDao
import com.gotham.app.data.local.entity.TicketEntity
import com.gotham.app.data.local.entity.VehicleEntity

@Database(
    entities = [VehicleEntity::class, TicketEntity::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun ticketDao(): TicketDao
}
