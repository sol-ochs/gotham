package com.aurox.gotham.util

object Constants {
    // API
    const val NYC_OPEN_DATA_BASE_URL = "https://data.cityofnewyork.us/resource/"
    const val API_TIMEOUT_SECONDS = 30L

    // Database
    const val DATABASE_NAME = "gotham_db"

    // WorkManager
    const val DEFAULT_CHECK_INTERVAL_HOURS = 24L
    const val DEFAULT_CHECK_HOUR = 8
    const val WORK_FLEX_INTERVAL_MINUTES = 15L

    // Vehicle
    const val MAX_VEHICLES = 5

    // Ticket Age Thresholds
    const val PAYABLE_TICKET_AGE_DAYS = 100L

    // Notification - New Tickets
    const val NOTIFICATION_CHANNEL_NEW_TICKETS_ID = "parking_tickets_channel"
    const val NOTIFICATION_CHANNEL_NEW_TICKETS_NAME = "Parking Tickets"
    const val NOTIFICATION_ID_NEW_TICKETS = 1001

    // Notification - Unpaid Reminders
    const val NOTIFICATION_CHANNEL_REMINDERS_ID = "unpaid_reminders_channel"
    const val NOTIFICATION_CHANNEL_REMINDERS_NAME = "Unpaid Ticket Reminders"
    const val NOTIFICATION_ID_UNPAID_REMINDER = 1002
    const val NOTIFICATION_ID_DEADLINE_REMINDER = 1003

    // Reminder Scheduling
    const val DEFAULT_REMINDER_HOUR = 10
    const val DEFAULT_REMINDER_INTERVAL_DAYS = 7L
    const val DEFAULT_DEADLINE_REMINDER_INTERVAL_DAYS = 1L

    // Preferences
    const val PREFS_NAME = "gotham_prefs"
    const val PREF_NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val PREF_REMINDERS_ENABLED = "reminders_enabled"
}
