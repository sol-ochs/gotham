package com.gotham.app.presentation.ticket.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gotham.app.R
import com.gotham.app.domain.model.Ticket
import com.gotham.app.presentation.theme.DarkerCardBackground
import com.gotham.app.presentation.theme.GoldenYellow
import com.gotham.app.util.Constants.PAYABLE_TICKET_AGE_DAYS
import java.time.LocalDateTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun TicketListScreen(
    onTicketClick: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: TicketListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()
    var showFilterSheet by remember { mutableStateOf(false) }
    val hasActiveFilters = state.selectedTypeFilter != TicketTypeFilter.ALL ||
                           state.selectedStatusFilter != TicketStatusFilter.ALL ||
                           state.selectedVehicleId != null

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing) {
            viewModel.refresh()
        }
    }

    LaunchedEffect(state.isRefreshing) {
        if (!state.isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

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
                            text = "TICKETS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    BadgedBox(
                        badge = {
                            if (hasActiveFilters) {
                                Badge(containerColor = GoldenYellow)
                            }
                        }
                    ) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    IconButton(onClick = viewModel::refresh) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TotalAmountCard(totalAmount = state.totalAmountOwed)
                }
                items(state.tickets, key = { it.summonsNumber }) { ticket ->
                    TicketCard(
                        ticket = ticket,
                        onClick = { onTicketClick(ticket.summonsNumber) }
                    )
                }
                if (state.selectedStatusFilter == TicketStatusFilter.UNPAID &&
                    (state.hiddenOlderUnpaidCount > 0 || state.showOlderUnpaid)
                ) {
                    item {
                        ShowOlderUnpaidToggle(
                            hiddenCount = state.hiddenOlderUnpaidCount,
                            showOlder = state.showOlderUnpaid,
                            onToggle = viewModel::onToggleShowOlderUnpaid
                        )
                    }
                }
            }

            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state.availableVehicles.isNotEmpty()) {
                    Text(
                        text = "Vehicle",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    VehicleFilterDropdown(
                        vehicles = state.availableVehicles,
                        selectedVehicleId = state.selectedVehicleId,
                        onVehicleSelected = { viewModel.onVehicleFilterChanged(it) }
                    )
                }

                Text(
                    text = "Type",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    TicketTypeFilter.entries.forEachIndexed { index, filter ->
                        SegmentedButton(
                            selected = state.selectedTypeFilter == filter,
                            onClick = { viewModel.onTypeFilterChanged(filter) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = TicketTypeFilter.entries.size
                            )
                        ) {
                            Text(filter.displayName)
                        }
                    }
                }

                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    TicketStatusFilter.entries.forEachIndexed { index, filter ->
                        SegmentedButton(
                            selected = state.selectedStatusFilter == filter,
                            onClick = { viewModel.onStatusFilterChanged(filter) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = TicketStatusFilter.entries.size
                            )
                        ) {
                            Text(filter.displayName)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleFilterDropdown(
    vehicles: List<com.gotham.app.domain.model.Vehicle>,
    selectedVehicleId: Long?,
    onVehicleSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedVehicle = vehicles.find { it.id == selectedVehicleId }

    Box {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedVehicle?.displayName ?: stringResource(R.string.ticket_filter_all),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.ticket_filter_all)) },
                onClick = {
                    onVehicleSelected(null)
                    expanded = false
                }
            )
            vehicles.forEach { vehicle ->
                DropdownMenuItem(
                    text = { Text(vehicle.displayName) },
                    onClick = {
                        onVehicleSelected(vehicle.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TicketCard(
    ticket: Ticket,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ticket.formattedViolation,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                val threshold = LocalDateTime.now().minusDays(PAYABLE_TICKET_AGE_DAYS)
                if (ticket.isNew && ticket.amountDue > 0 && ticket.issueDateTime.isAfter(threshold)) {
                    Badge(
                        containerColor = GoldenYellow,
                        contentColor = Color.Black,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(stringResource(R.string.ticket_new_badge))
                    }
                }
            }
            Text(
                text = ticket.plate,
                style = MaterialTheme.typography.bodyMedium,
                color = GoldenYellow
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ticket.formattedIssueDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "\u2022",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = ticket.formattedFineAmount,
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldenYellow
                )
            }
        }
    }
}

@Composable
private fun TotalAmountCard(totalAmount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DarkerCardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Amount Owed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format(Locale.US, "$%.2f", totalAmount),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = GoldenYellow
            )
        }
    }
}

@Composable
private fun ShowOlderUnpaidToggle(
    hiddenCount: Int,
    showOlder: Boolean,
    onToggle: () -> Unit
) {
    TextButton(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (showOlder) {
                "Hide older unpaid tickets"
            } else {
                "Show $hiddenCount older unpaid (90+ days)"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = GoldenYellow
        )
    }
}
