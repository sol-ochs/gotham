package com.gotham.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object TicketList : Screen("ticket_list")
    data object VehicleList : Screen("vehicle_list")
    data object Settings : Screen("settings")

    data object AddEditVehicle : Screen("add_edit_vehicle/{vehicleId}") {
        fun createRoute(vehicleId: Long? = null): String {
            return "add_edit_vehicle/${vehicleId ?: "new"}"
        }

        const val ARG_VEHICLE_ID = "vehicleId"
    }

    data object TicketDetail : Screen("ticket_detail/{summonsNumber}") {
        fun createRoute(summonsNumber: String): String {
            return "ticket_detail/$summonsNumber"
        }

        const val ARG_SUMMONS_NUMBER = "summonsNumber"
    }
}
