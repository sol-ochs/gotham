package com.aurox.gotham.data.worker

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.aurox.gotham.util.Constants
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun schedulePeriodicTicketCheck(intervalHours: Long = Constants.DEFAULT_CHECK_INTERVAL_HOURS) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val initialDelay = calculateDelayUntilNextCheckTime()

        val workRequest = PeriodicWorkRequestBuilder<TicketCheckWorker>(
            intervalHours, TimeUnit.HOURS,
            Constants.WORK_FLEX_INTERVAL_MINUTES, TimeUnit.MINUTES
        )
            .setInitialDelay(initialDelay.toMinutes(), TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            TicketCheckWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun calculateDelayUntilNextCheckTime(): Duration {
        val now = LocalDateTime.now()
        val targetTime = LocalTime.of(Constants.DEFAULT_CHECK_HOUR, 0)
        var nextRun = now.toLocalDate().atTime(targetTime)
        if (now >= nextRun) {
            nextRun = nextRun.plusDays(1)
        }
        return Duration.between(now, nextRun)
    }

    fun cancelPeriodicTicketCheck() {
        workManager.cancelUniqueWork(TicketCheckWorker.WORK_NAME)
    }

    fun scheduleUnpaidReminder() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val initialDelay = calculateDelayUntilReminderTime()

        val workRequest = PeriodicWorkRequestBuilder<UnpaidReminderWorker>(
            Constants.DEFAULT_REMINDER_INTERVAL_DAYS, TimeUnit.DAYS,
            Constants.WORK_FLEX_INTERVAL_MINUTES, TimeUnit.MINUTES
        )
            .setInitialDelay(initialDelay.toMinutes(), TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            UnpaidReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelUnpaidReminder() {
        workManager.cancelUniqueWork(UnpaidReminderWorker.WORK_NAME)
    }

    fun scheduleDeadlineReminder() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val initialDelay = calculateDelayUntilReminderTime()

        val workRequest = PeriodicWorkRequestBuilder<DeadlineReminderWorker>(
            Constants.DEFAULT_DEADLINE_REMINDER_INTERVAL_DAYS, TimeUnit.DAYS,
            Constants.WORK_FLEX_INTERVAL_MINUTES, TimeUnit.MINUTES
        )
            .setInitialDelay(initialDelay.toMinutes(), TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            DeadlineReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelDeadlineReminder() {
        workManager.cancelUniqueWork(DeadlineReminderWorker.WORK_NAME)
    }

    private fun calculateDelayUntilReminderTime(): Duration {
        val now = LocalDateTime.now()
        val targetTime = LocalTime.of(Constants.DEFAULT_REMINDER_HOUR, 0)
        var nextRun = now.toLocalDate().atTime(targetTime)
        if (now >= nextRun) {
            nextRun = nextRun.plusDays(1)
        }
        return Duration.between(now, nextRun)
    }
}
