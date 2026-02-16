package com.aurox.gotham.di

import android.content.Context
import androidx.room.Room
import com.aurox.gotham.data.local.AppDatabase
import com.aurox.gotham.data.local.dao.DeadlineReminderEventDao
import com.aurox.gotham.data.local.dao.TicketDao
import com.aurox.gotham.data.local.dao.VehicleDao
import com.aurox.gotham.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .addMigrations(*com.aurox.gotham.data.local.migration.Migrations.ALL)
            .build()
    }

    @Provides
    @Singleton
    fun provideVehicleDao(database: AppDatabase): VehicleDao {
        return database.vehicleDao()
    }

    @Provides
    @Singleton
    fun provideTicketDao(database: AppDatabase): TicketDao {
        return database.ticketDao()
    }

    @Provides
    @Singleton
    fun provideDeadlineReminderEventDao(database: AppDatabase): DeadlineReminderEventDao {
        return database.deadlineReminderEventDao()
    }
}
