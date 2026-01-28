package com.gotham.app.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gotham.app.domain.model.Ticket
import com.gotham.app.util.notification.NotificationHelper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.time.LocalDateTime

class DebugNotificationReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface NotificationHelperEntryPoint {
        fun notificationHelper(): NotificationHelper
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received broadcast: ${intent.action}")
        if (intent.action != ACTION_TEST_NOTIFICATION) return

        val ticketCount = intent.getIntExtra(EXTRA_TICKET_COUNT, 1).coerceIn(1, 10)
        val fineAmount = intent.getFloatExtra(EXTRA_FINE_AMOUNT, 65f).toDouble()
        val violation = intent.getStringExtra(EXTRA_VIOLATION) ?: "NO PARKING-STREET CLEANING"

        Log.d(TAG, "Creating $ticketCount test ticket(s)")

        val tickets = (1..ticketCount).map { index ->
            createFakeTicket(index, fineAmount, violation)
        }

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            NotificationHelperEntryPoint::class.java
        )
        entryPoint.notificationHelper().showNewTicketsNotification(tickets)
        Log.d(TAG, "Notification sent")
    }

    private fun createFakeTicket(index: Int, fineAmount: Double, violation: String): Ticket {
        return Ticket(
            summonsNumber = "TEST${System.currentTimeMillis()}$index",
            vehicleId = 1L,
            plate = "TEST${index}23",
            state = "NY",
            licenseType = "PAS",
            issueDateTime = LocalDateTime.now().minusHours(index.toLong()),
            violation = violation,
            violationLocation = "123 TEST STREET",
            fineAmount = fineAmount,
            amountDue = fineAmount,
            violationStatus = "OUTSTANDING",
            penaltyAmount = null,
            interestAmount = null,
            paymentAmount = null,
            isNew = true
        )
    }

    companion object {
        private const val TAG = "DebugNotification"
        const val ACTION_TEST_NOTIFICATION = "com.gotham.app.debug.TEST_NOTIFICATION"
        private const val EXTRA_TICKET_COUNT = "ticket_count"
        private const val EXTRA_FINE_AMOUNT = "fine_amount"
        private const val EXTRA_VIOLATION = "violation"
    }
}
