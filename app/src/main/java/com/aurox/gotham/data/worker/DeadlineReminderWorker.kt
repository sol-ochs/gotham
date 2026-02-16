package com.aurox.gotham.data.worker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aurox.gotham.domain.usecase.ticket.GetDeadlineReminderDataUseCase
import com.aurox.gotham.domain.repository.TicketRepository
import com.aurox.gotham.util.Constants
import com.aurox.gotham.util.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class DeadlineReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getDeadlineReminderDataUseCase: GetDeadlineReminderDataUseCase,
    private val ticketRepository: TicketRepository,
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

            val reminderData = getDeadlineReminderDataUseCase()
            if (reminderData.count <= 0) {
                return Result.success()
            }

            notificationHelper.showDeadlineReminderNotification(
                reminderData.count,
                reminderData.nearestDaysLeft
            )

            val now = System.currentTimeMillis()
            reminderData.targets.forEach { target ->
                ticketRepository.recordDeadlineReminderEvent(
                    summonsNumber = target.summonsNumber,
                    milestoneDay = target.milestoneDay,
                    sentAt = now
                )
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
        const val WORK_NAME = "deadline_reminder_work"
    }
}
