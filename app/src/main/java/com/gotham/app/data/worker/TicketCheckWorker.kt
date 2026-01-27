package com.gotham.app.data.worker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gotham.app.domain.usecase.sync.CheckForNewTicketsUseCase
import com.gotham.app.util.Constants
import com.gotham.app.util.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@HiltWorker
class TicketCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val checkForNewTicketsUseCase: CheckForNewTicketsUseCase,
    private val notificationHelper: NotificationHelper,
    private val dataStore: DataStore<Preferences>
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            when (val result = checkForNewTicketsUseCase()) {
                is com.gotham.app.domain.util.Result.Success -> {
                    val newTickets = result.data
                    val notificationsKey = booleanPreferencesKey(Constants.PREF_NOTIFICATIONS_ENABLED)
                    val notificationsEnabled = dataStore.data
                        .map { prefs -> prefs[notificationsKey] != false }
                        .first()

                    if (newTickets.isNotEmpty() && notificationsEnabled) {
                        notificationHelper.showNewTicketsNotification(newTickets)
                    }

                    Result.success()
                }
                is com.gotham.app.domain.util.Result.Error -> {
                    if (runAttemptCount < 3) {
                        Result.retry()
                    } else {
                        Result.failure()
                    }
                }
                is com.gotham.app.domain.util.Result.Loading -> {
                    Result.success()
                }
            }
        } catch (_: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        const val WORK_NAME = "ticket_check_work"
    }
}
