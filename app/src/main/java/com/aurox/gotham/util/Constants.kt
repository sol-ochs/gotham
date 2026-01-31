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

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "parking_tickets_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Parking Tickets"
    const val NOTIFICATION_ID_NEW_TICKETS = 1001

    // Preferences
    const val PREFS_NAME = "gotham_prefs"
    const val PREF_NOTIFICATIONS_ENABLED = "notifications_enabled"
}
