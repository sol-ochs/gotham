package com.gotham.app.domain.model

import android.util.Log

enum class VehicleType(val code: String, val displayName: String) {
    PASSENGER("PAS", "Passenger"),
    COMMERCIAL("COM", "Commercial"),
    MOTORCYCLE("MOT", "Motorcycle"),
    TRUCK("TRK", "Truck"),
    TRAILER("TRL", "Trailer"),
    VAN("VAN", "Van"),
    TAXI("TAX", "Taxi"),
    LIMOUSINE("LIM", "Limousine"),
    AMBULANCE("AMB", "Ambulance"),
    BUS("BUS", "Bus"),
    OTHER("OTH", "Other"),
    UNSPECIFIED("UNS", "Unspecified");

    companion object {
        private const val TAG = "VehicleType"

        fun fromCode(code: String): VehicleType {
            val found = entries.find { it.code.equals(code, ignoreCase = true) }
            if (found == null) {
                Log.w(TAG, "Unknown vehicle type code: $code")
                return UNSPECIFIED
            }
            return found
        }
    }
}
