package com.gotham.app.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gotham.app.domain.usecase.sync.CheckForNewTicketsUseCase
import com.gotham.app.domain.util.Result
import com.gotham.app.util.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TicketCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val checkForNewTicketsUseCase: CheckForNewTicketsUseCase,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            when (val result = checkForNewTicketsUseCase()) {
                is com.gotham.app.domain.util.Result.Success -> {
                    val newTickets = result.data

                    if (newTickets.isNotEmpty()) {
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
        } catch (e: Exception) {
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
