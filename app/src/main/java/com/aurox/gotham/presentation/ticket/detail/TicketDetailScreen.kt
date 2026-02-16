package com.aurox.gotham.presentation.ticket.detail

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurox.gotham.R
import com.aurox.gotham.presentation.theme.BlueAccent
import com.aurox.gotham.presentation.theme.GoldenYellow

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
                            text = "TICKET DETAILS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
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
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = ticket.formattedViolation,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
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
                            value = ticket.formattedFineAmount,
                            valueColor = GoldenYellow
                        )
                        DetailRow(
                            label = stringResource(R.string.ticket_amount_due),
                            value = ticket.formattedEffectiveAmountDue,
                            valueColor = GoldenYellow
                        )
                        ticket.adjudicationStatus?.let {
                            DetailRow(
                                label = stringResource(R.string.ticket_adjudication_status),
                                value = it
                            )
                        }
                        if (ticket.isPaidOverride) {
                            Text(
                                text = stringResource(R.string.ticket_user_marked_note),
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldenYellow
                            )
                        }
                    }
                }

                if (ticket.amountDue > 0.0 || ticket.isPaidOverride) {
                    Button(
                        onClick = viewModel::togglePaidOverride,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldenYellow,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (ticket.isPaidOverride) {
                                stringResource(R.string.ticket_undo_mark_paid_local)
                            } else {
                                stringResource(R.string.ticket_mark_paid_local)
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Button(
                    onClick = { uriHandler.openUri(buildSummonsUrl(ticket.summonsNumber)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueAccent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = stringResource(R.string.ticket_view_summons),
                        style = MaterialTheme.typography.titleMedium
                    )
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
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
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
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor
        )
    }
}
