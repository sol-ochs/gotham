package com.aurox.gotham.data.worker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aurox.gotham.domain.usecase.ticket.GetUnpaidReminderDataUseCase
import com.aurox.gotham.util.Constants
import com.aurox.gotham.util.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class UnpaidReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getUnpaidReminderDataUseCase: GetUnpaidReminderDataUseCase,
    private val notificationHelper: NotificationHelper,
    private val dataStore: DataStore<Preferences>
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val notificationsKey = booleanPreferencesKey(Constants.PREF_NOTIFICATIONS_ENABLED)
            val remindersKey = booleanPreferencesKey(Constants.PREF_REMINDERS_ENABLED)

            val prefs = dataStore.data.first()
            val notificationsEnabled = prefs[notificationsKey] != false
            val remindersEnabled = prefs[remindersKey] != false

            if (!notificationsEnabled || !remindersEnabled) {
                return Result.success()
            }

            val reminderData = getUnpaidReminderDataUseCase()

            if (reminderData.count > 0) {
                notificationHelper.showUnpaidReminderNotification(reminderData.count)
            }

            Result.success()
        } catch (_: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        const val WORK_NAME = "unpaid_reminder_work"
    }
}
