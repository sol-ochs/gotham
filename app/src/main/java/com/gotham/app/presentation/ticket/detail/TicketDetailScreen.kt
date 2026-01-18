package com.gotham.app.presentation.ticket.detail

import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gotham.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    onBack: () -> Unit,
    viewModel: TicketDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.ticket_detail_title),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        state.ticket?.let { ticket ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = ticket.formattedViolation,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DetailRow(
                            label = stringResource(R.string.ticket_summons_number),
                            value = ticket.summonsNumber
                        )
                        DetailRow(
                            label = stringResource(R.string.vehicle_license_plate),
                            value = "${ticket.plate} (${ticket.state})"
                        )
                        DetailRow(
                            label = stringResource(R.string.ticket_date),
                            value = ticket.formattedIssueDate
                        )
                        DetailRow(
                            label = stringResource(R.string.ticket_time),
                            value = ticket.formattedIssueTime
                        )
                        ticket.violationLocation?.let {
                            DetailRow(
                                label = stringResource(R.string.ticket_location),
                                value = it
                            )
                        }
                        DetailRow(
                            label = stringResource(R.string.ticket_fine_amount),
                            value = ticket.formattedFineAmount
                        )
                        DetailRow(
                            label = stringResource(R.string.ticket_amount_due),
                            value = ticket.formattedAmountDue
                        )
                        ticket.violationStatus?.let {
                            DetailRow(
                                label = stringResource(R.string.ticket_status),
                                value = it
                            )
                        }
                    }
                }

                Button(
                    onClick = { uriHandler.openUri(buildSummonsUrl(ticket.summonsNumber)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(stringResource(R.string.ticket_view_summons))
                }
            }
        }
    }
}

private fun buildSummonsUrl(summonsNumber: String): String {
    val encoded = summonsNumber.toByteArray()
        .let { Base64.encodeToString(it, Base64.NO_WRAP) }
        .toByteArray()
        .let { Base64.encodeToString(it, Base64.NO_WRAP) }
        .toByteArray()
        .let { Base64.encodeToString(it, Base64.NO_WRAP) }
    return "https://nycserv.nyc.gov/NYCServWeb/ShowImage?searchID=$encoded&locationName=_____________________"
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
