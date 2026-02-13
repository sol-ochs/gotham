package com.aurox.gotham.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.aurox.gotham.BuildConfig
import com.aurox.gotham.R
import com.aurox.gotham.data.worker.WorkManagerScheduler
import com.aurox.gotham.presentation.theme.GoldenYellow
import com.aurox.gotham.util.Constants
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
private fun gothamSwitchColors() = SwitchDefaults.colors(
    checkedThumbColor = Color.White,
    checkedTrackColor = GoldenYellow,
    uncheckedThumbColor = Color.White,
    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
    disabledCheckedThumbColor = Color.White.copy(alpha = 0.7f),
    disabledCheckedTrackColor = GoldenYellow.copy(alpha = 0.5f),
    disabledUncheckedThumbColor = Color.White.copy(alpha = 0.7f),
    disabledUncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    dataStore: DataStore<Preferences>,
    workManagerScheduler: WorkManagerScheduler
) {
    val scope = rememberCoroutineScope()
    val notificationsKey = booleanPreferencesKey(Constants.PREF_NOTIFICATIONS_ENABLED)
    val remindersKey = booleanPreferencesKey(Constants.PREF_REMINDERS_ENABLED)

    val notificationsFlow = remember {
        dataStore.data.map { prefs -> prefs[notificationsKey] != false }
    }
    val remindersFlow = remember {
        dataStore.data.map { prefs -> prefs[remindersKey] != false }
    }

    val notificationsEnabled by notificationsFlow.collectAsState(initial = true)
    val remindersEnabled by remindersFlow.collectAsState(initial = true)

    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showTermsOfServiceDialog by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "GOTHAM",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = GoldenYellow
                        )
                        Text(
                            text = "SETTINGS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.settings_notifications),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.settings_enable_notifications),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { enabled ->
                                scope.launch {
                                    dataStore.edit { prefs ->
                                        prefs[notificationsKey] = enabled
                                    }
                                    if (enabled) {
                                        workManagerScheduler.schedulePeriodicTicketCheck()
                                        if (remindersEnabled) {
                                            workManagerScheduler.scheduleUnpaidReminder()
                                        }
                                    } else {
                                        workManagerScheduler.cancelPeriodicTicketCheck()
                                        workManagerScheduler.cancelUnpaidReminder()
                                    }
                                }
                            },
                            thumbContent = {
                                Box(modifier = Modifier.size(16.dp))
                            },
                            colors = gothamSwitchColors()
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    )
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.settings_enable_reminders),
                            color = if (notificationsEnabled) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            }
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.settings_reminders_description),
                            color = if (notificationsEnabled) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                            }
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = remindersEnabled,
                            onCheckedChange = { enabled ->
                                scope.launch {
                                    dataStore.edit { prefs ->
                                        prefs[remindersKey] = enabled
                                    }
                                    if (enabled) {
                                        workManagerScheduler.scheduleUnpaidReminder()
                                    } else {
                                        workManagerScheduler.cancelUnpaidReminder()
                                    }
                                }
                            },
                            enabled = notificationsEnabled,
                            thumbContent = {
                                Box(modifier = Modifier.size(16.dp))
                            },
                            colors = gothamSwitchColors()
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.settings_about),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = "Version",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    trailingContent = {
                        Text(
                            text = BuildConfig.VERSION_NAME,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.settings_legal),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.settings_privacy_policy),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.clickable { showPrivacyPolicyDialog = true }
                )
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.settings_terms_of_service),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.clickable { showTermsOfServiceDialog = true }
                )
            }
        }
    }

    if (showPrivacyPolicyDialog) {
        LegalTextDialog(
            title = stringResource(R.string.settings_privacy_policy),
            content = stringResource(R.string.privacy_policy_content),
            onDismiss = { showPrivacyPolicyDialog = false }
        )
    }

    if (showTermsOfServiceDialog) {
        LegalTextDialog(
            title = stringResource(R.string.settings_terms_of_service),
            content = stringResource(R.string.terms_of_service_content),
            onDismiss = { showTermsOfServiceDialog = false }
        )
    }

}

@Composable
private fun LegalTextDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(text = content)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
