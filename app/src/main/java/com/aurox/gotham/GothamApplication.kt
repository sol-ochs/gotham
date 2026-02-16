package com.aurox.gotham

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.aurox.gotham.util.Constants
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GothamApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NotificationManager::class.java)

        val ticketsChannel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_NEW_TICKETS_ID,
            Constants.NOTIFICATION_CHANNEL_NEW_TICKETS_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for new parking tickets"
            enableVibration(true)
            enableLights(true)
        }

        val remindersChannel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_REMINDERS_ID,
            Constants.NOTIFICATION_CHANNEL_REMINDERS_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Periodic reminders for unpaid tickets"
        }

        notificationManager.createNotificationChannel(ticketsChannel)
        notificationManager.createNotificationChannel(remindersChannel)
    }
}
