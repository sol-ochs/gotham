package com.gotham.app.util

object Constants {
    // API
    const val NYC_OPEN_DATA_BASE_URL = "https://data.cityofnewyork.us/resource/"
    const val NYC_API_DATASET_ID = "nc67-uf89"
    const val API_TIMEOUT_SECONDS = 30L
    const val API_RATE_LIMIT_PER_HOUR = 1000

    // Database
    const val DATABASE_NAME = "gotham_db"
    const val DATABASE_VERSION = 1

    // WorkManager
    const val TICKET_CHECK_WORK_NAME = "ticket_check_work"
    const val DEFAULT_CHECK_INTERVAL_HOURS = 6L
    const val WORK_FLEX_INTERVAL_MINUTES = 15L

    // Vehicle
    const val MAX_VEHICLES = 5

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "parking_tickets_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Parking Tickets"
    const val NOTIFICATION_ID_NEW_TICKETS = 1001

    // Deep Links
    const val DEEP_LINK_SCHEME = "gotham"
    const val DEEP_LINK_HOST_TICKET = "ticket"

    // Preferences
    const val PREFS_NAME = "gotham_prefs"
    const val PREF_CHECK_FREQUENCY_HOURS = "check_frequency_hours"
    const val PREF_NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val PREF_LAST_CHECK_TIME = "last_check_time"
    const val PREF_ONBOARDING_COMPLETED = "onboarding_completed"

    // Date formats
    const val DATE_FORMAT_DISPLAY = "MMM dd, yyyy"
    const val TIME_FORMAT_DISPLAY = "hh:mm a"
}
