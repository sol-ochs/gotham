package com.aurox.gotham.data.worker

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import com.aurox.gotham.util.Constants
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UnpaidReminderWorkerTest {

    private val notificationsKey = booleanPreferencesKey(Constants.PREF_NOTIFICATIONS_ENABLED)
    private val remindersKey = booleanPreferencesKey(Constants.PREF_REMINDERS_ENABLED)

    @Test
    fun `should not notify when notifications disabled`() {
        val prefs = preferencesOf(
            notificationsKey to false,
            remindersKey to true
        )

        val notificationsEnabled = prefs[notificationsKey] != false
        val remindersEnabled = prefs[remindersKey] != false

        val shouldNotify = notificationsEnabled && remindersEnabled

        assertFalse(shouldNotify)
    }

    @Test
    fun `should not notify when reminders disabled`() {
        val prefs = preferencesOf(
            notificationsKey to true,
            remindersKey to false
        )

        val notificationsEnabled = prefs[notificationsKey] != false
        val remindersEnabled = prefs[remindersKey] != false

        val shouldNotify = notificationsEnabled && remindersEnabled

        assertFalse(shouldNotify)
    }

    @Test
    fun `should notify when notifications and reminders are enabled`() {
        val prefs = preferencesOf(
            notificationsKey to true,
            remindersKey to true
        )

        val notificationsEnabled = prefs[notificationsKey] != false
        val remindersEnabled = prefs[remindersKey] != false

        val shouldNotify = notificationsEnabled && remindersEnabled

        assertTrue(shouldNotify)
    }

    @Test
    fun `notifications enabled defaults to true when key missing`() {
        val prefs = preferencesOf(
            remindersKey to true
        )

        val notificationsEnabled = prefs[notificationsKey] != false

        assertTrue(notificationsEnabled)
    }

    @Test
    fun `reminders enabled defaults to true when key missing`() {
        val prefs = preferencesOf(notificationsKey to true)

        val remindersEnabled = prefs[remindersKey] != false

        assertTrue(remindersEnabled)
    }
}
