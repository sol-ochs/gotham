package com.aurox.gotham.presentation.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object TicketList : Screen("ticket_list?vehicleId={vehicleId}&statusFilter={statusFilter}") {
        fun createRoute(vehicleId: Long? = null, statusFilter: String? = null): String {
            val params = mutableListOf<String>()
            if (vehicleId != null) params.add("vehicleId=$vehicleId")
            if (statusFilter != null) params.add("statusFilter=$statusFilter")
            return if (params.isNotEmpty()) {
                "ticket_list?${params.joinToString("&")}"
            } else {
                "ticket_list"
            }
        }

        const val ARG_VEHICLE_ID = "vehicleId"
        const val ARG_STATUS_FILTER = "statusFilter"
    }
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
