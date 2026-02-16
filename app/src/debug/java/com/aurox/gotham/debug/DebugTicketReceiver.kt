package com.aurox.gotham.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.aurox.gotham.data.local.dao.TicketDao
import com.aurox.gotham.data.local.dao.VehicleDao
import com.aurox.gotham.data.local.entity.TicketEntity
import com.aurox.gotham.domain.model.Ticket
import com.aurox.gotham.domain.usecase.ticket.GetDeadlineReminderDataUseCase
import com.aurox.gotham.domain.usecase.ticket.GetUnpaidReminderDataUseCase
import com.aurox.gotham.util.notification.NotificationHelper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val ISO_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

class DebugTicketReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DebugTicketEntryPoint {
        fun notificationHelper(): NotificationHelper
        fun vehicleDao(): VehicleDao
        fun ticketDao(): TicketDao
        fun getUnpaidReminderDataUseCase(): GetUnpaidReminderDataUseCase
        fun getDeadlineReminderDataUseCase(): GetDeadlineReminderDataUseCase
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received broadcast: ${intent.action}")

        when (intent.action) {
            ACTION_TEST_TICKET -> handleTestTicket(context, intent)
            ACTION_TRIGGER_UNPAID_REMINDER -> handleTriggerUnpaidReminder(context)
            ACTION_TRIGGER_DEADLINE_REMINDER -> handleTriggerDeadlineReminder(context)
            else -> return
        }
    }

    private fun handleTriggerDeadlineReminder(context: Context) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            DebugTicketEntryPoint::class.java
        )

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val useCase = entryPoint.getDeadlineReminderDataUseCase()
                val data = useCase()
                Log.d(
                    TAG,
                    "Deadline reminder data: count=${data.count}, nearestDaysLeft=${data.nearestDaysLeft}"
                )

                if (data.count > 0) {
                    entryPoint.notificationHelper().showDeadlineReminderNotification(
                        data.count,
                        data.nearestDaysLeft
                    )
                    Log.d(TAG, "Deadline reminder notification sent")
                } else {
                    Log.d(TAG, "No deadline reminder tickets, skipping notification")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to trigger deadline reminder", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun handleTriggerUnpaidReminder(context: Context) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            DebugTicketEntryPoint::class.java
        )

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val useCase = entryPoint.getUnpaidReminderDataUseCase()
                val data = useCase()
                Log.d(TAG, "Unpaid reminder data: count=${data.count}, total=${data.totalAmount}")

                if (data.count > 0) {
                    entryPoint.notificationHelper().showUnpaidReminderNotification(data.count)
                    Log.d(TAG, "Unpaid reminder notification sent")
                } else {
                    Log.d(TAG, "No unpaid reminder tickets, skipping notification")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to trigger unpaid reminder", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun handleTestTicket(context: Context, intent: Intent) {
        val plate = intent.getStringExtra(EXTRA_PLATE)
        if (plate.isNullOrBlank()) {
            Log.e(TAG, "Missing required 'plate' parameter")
            return
        }

        val ticketCount = intent.getIntExtra(EXTRA_TICKET_COUNT, 1).coerceIn(1, 10)
        val fineAmount = intent.getFloatExtra(EXTRA_FINE_AMOUNT, 65f).toDouble()
        val violation = intent.getStringExtra(EXTRA_VIOLATION) ?: "NO PARKING-STREET CLEANING"

        Log.d(TAG, "Creating $ticketCount test ticket(s) for plate: $plate")

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            DebugTicketEntryPoint::class.java
        )

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val vehicle = entryPoint.vehicleDao().getVehicleByPlate(plate)
                if (vehicle == null) {
                    Log.e(TAG, "Vehicle not found for plate: $plate")
                    return@launch
                }

                val now = LocalDateTime.now()
                val tickets = (1..ticketCount).map { index ->
                    createTicketEntity(
                        index = index,
                        vehicleId = vehicle.id,
                        plate = vehicle.licensePlate,
                        state = vehicle.state,
                        fineAmount = fineAmount,
                        violation = violation,
                        issueTime = now.minusHours(index.toLong())
                    )
                }

                entryPoint.ticketDao().insertTickets(tickets)
                Log.d(TAG, "Inserted ${tickets.size} ticket(s) into database")

                val domainTickets = tickets.map { it.toDomainTicket() }
                entryPoint.notificationHelper().showNewTicketsNotification(domainTickets)
                Log.d(TAG, "Notification sent")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create test tickets", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun createTicketEntity(
        index: Int,
        vehicleId: Long,
        plate: String,
        state: String,
        fineAmount: Double,
        violation: String,
        issueTime: LocalDateTime
    ): TicketEntity {
        return TicketEntity(
            summonsNumber = "TEST${System.currentTimeMillis()}$index",
            vehicleId = vehicleId,
            plate = plate,
            state = state,
            licenseType = "PAS",
            issueDateTime = issueTime.format(ISO_FORMATTER),
            violation = violation,
            violationLocation = "123 TEST STREET",
            fineAmount = fineAmount,
            amountDue = fineAmount,
            adjudicationStatus = null,
            penaltyAmount = null,
            interestAmount = null,
            paymentAmount = null,
            isNew = true
        )
    }

    private fun TicketEntity.toDomainTicket(): Ticket {
        return Ticket(
            summonsNumber = summonsNumber,
            vehicleId = vehicleId,
            plate = plate,
            state = state,
            licenseType = licenseType,
            issueDateTime = LocalDateTime.parse(issueDateTime, ISO_FORMATTER),
            violation = violation,
            violationLocation = violationLocation,
            fineAmount = fineAmount,
            amountDue = amountDue,
            adjudicationStatus = adjudicationStatus,
            penaltyAmount = penaltyAmount,
            interestAmount = interestAmount,
            paymentAmount = paymentAmount,
            isNew = isNew
        )
    }

    companion object {
        private const val TAG = "DebugTicket"
        private const val ACTION_TEST_TICKET = "com.aurox.gotham.debug.TEST_TICKET"
        private const val ACTION_TRIGGER_UNPAID_REMINDER = "com.aurox.gotham.debug.TRIGGER_UNPAID_REMINDER"
        private const val ACTION_TRIGGER_DEADLINE_REMINDER = "com.aurox.gotham.debug.TRIGGER_DEADLINE_REMINDER"
        private const val EXTRA_PLATE = "plate"
        private const val EXTRA_TICKET_COUNT = "ticket_count"
        private const val EXTRA_FINE_AMOUNT = "fine_amount"
        private const val EXTRA_VIOLATION = "violation"
    }
}
