package com.gotham.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.gotham.app.presentation.theme.DarkerCardBackground
import com.gotham.app.presentation.theme.GoldenYellow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.gotham.app.R
import com.gotham.app.presentation.home.HomeScreen
import com.gotham.app.presentation.onboarding.OnboardingScreen
import com.gotham.app.presentation.settings.SettingsScreen
import com.gotham.app.presentation.ticket.detail.TicketDetailScreen
import com.gotham.app.presentation.ticket.list.TicketListScreen
import com.gotham.app.presentation.vehicle.add.AddEditVehicleScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Settings.route
    ) || currentRoute?.startsWith("ticket_list") == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                val navItemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GoldenYellow,
                    selectedTextColor = GoldenYellow,
                    indicatorColor = DarkerCardBackground,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
                NavigationBar(
                    containerColor = DarkerCardBackground
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            if (currentRoute != Screen.Home.route) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text(stringResource(R.string.nav_home)) },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = currentRoute?.startsWith("ticket_list") == true,
                        onClick = {
                            if (currentRoute?.startsWith("ticket_list") != true) {
                                navController.navigate(Screen.TicketList.createRoute()) {
                                    popUpTo(Screen.Home.route)
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Receipt, null) },
                        label = { Text(stringResource(R.string.nav_tickets)) },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Settings.route,
                        onClick = {
                            if (currentRoute != Screen.Settings.route) {
                                navController.navigate(Screen.Settings.route) {
                                    popUpTo(Screen.Home.route)
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Settings, null) },
                        label = { Text(stringResource(R.string.nav_settings)) },
                        colors = navItemColors
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Screen.AddEditVehicle.createRoute()) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddVehicle = {
                    navController.navigate(Screen.AddEditVehicle.createRoute())
                },
                onNavigateToTickets = { vehicleId ->
                    navController.navigate(Screen.TicketList.createRoute(vehicleId, "UNPAID"))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.TicketList.route,
            arguments = listOf(
                navArgument(Screen.TicketList.ARG_VEHICLE_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(Screen.TicketList.ARG_STATUS_FILTER) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            TicketListScreen(
                onTicketClick = { summonsNumber ->
                    navController.navigate(Screen.TicketDetail.createRoute(summonsNumber))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.AddEditVehicle.route,
            arguments = listOf(
                navArgument(Screen.AddEditVehicle.ARG_VEHICLE_ID) {
                    type = NavType.StringType
                    defaultValue = "new"
                }
            )
        ) {
            AddEditVehicleScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onVehicleSaved = { isFirstVehicle ->
                    if (isFirstVehicle) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    } else {
                        navController.navigateUp()
                    }
                }
            )
        }

        composable(
            route = Screen.TicketDetail.route,
            arguments = listOf(
                navArgument(Screen.TicketDetail.ARG_SUMMONS_NUMBER) {
                    type = NavType.StringType
                }
            )
        ) {
            TicketDetailScreen(
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = {
                    navController.navigateUp()
                }
            )
        }
    }
    }
}
