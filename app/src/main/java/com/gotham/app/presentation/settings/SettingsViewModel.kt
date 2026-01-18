package com.gotham.app.presentation.settings

import androidx.lifecycle.ViewModel
import com.gotham.app.domain.model.Ticket
import com.gotham.app.util.notification.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    // TODO: Remove before release
    fun testNotification() {
        val fakeTicket = Ticket(
            summonsNumber = "TEST123",
            vehicleId = 0,
            plate = "TEST123",
            state = "NY",
            licenseType = "PAS",
            issueDateTime = LocalDateTime.of(2025, 1, 14, 12, 0),
            violation = "NO PARKING-STREET CLEANING",
            violationLocation = "123 TEST ST",
            fineAmount = 65.0,
            amountDue = 65.0,
            violationStatus = "OUTSTANDING",
            penaltyAmount = null,
            interestAmount = null,
            paymentAmount = null,
            isNew = true
        )
        notificationHelper.showNewTicketsNotification(listOf(fakeTicket))
    }
}
