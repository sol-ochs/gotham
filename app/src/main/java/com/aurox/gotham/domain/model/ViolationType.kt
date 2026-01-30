package com.aurox.gotham.domain.model

import android.util.Log

enum class ViolationType(val code: String, val displayName: String) {
    FAILURE_TO_DISPLAY_BUS_PERMIT("FAILURE TO DISPLAY BUS PERMIT", "Bus Permit Not Displayed"),
    NO_OPERATOR_INFO_DISPLAY("NO OPERATOR NAM/ADD/PH DISPLAY", "No Operator Info Displayed"),
    UNAUTHORIZED_PASSENGER_PICKUP("UNAUTHORIZED PASSENGER PICK-UP", "Unauthorized Passenger Pick-Up"),
    BUS_PARKING_LOWER_MANHATTAN("BUS PARKING IN LOWER MANHATTAN", "Bus Parking in Lower Manhattan"),
    BUS_LANE_VIOLATION("BUS LANE VIOLATION", "Bus Lane Violation"),
    OVERNIGHT_TRACTOR_TRAILER("OVERNIGHT TRACTOR TRAILER PKG", "Overnight Tractor Trailer Parking"),
    FAILURE_TO_STOP_RED_LIGHT("FAILURE TO STOP AT RED LIGHT", "Red Light Violation"),
    IDLING("IDLING", "Idling"),
    OBSTRUCTING_TRAFFIC("OBSTRUCTING TRAFFIC/INTERSECT", "Obstructing Traffic"),
    NO_STOPPING_DAY_TIME("NO STOPPING-DAY/TIME LIMITS", "No Stopping Zone"),
    NO_STANDING_HOTEL("NO STANDING-HOTEL LOADING", "No Standing - Hotel Loading"),
    MOBILE_BUS_LANE("MOBILE BUS LANE VIOLATION", "Mobile Bus Lane Violation"),
    NO_STANDING_TAXI_STAND("NO STANDING-TAXI STAND", "No Standing - Taxi Stand"),
    NO_STANDING_DAY_TIME("NO STANDING-DAY/TIME LIMITS", "No Standing Zone"),
    NO_STANDING_OFF_STREET("NO STANDING-OFF-STREET LOT", "No Standing - Off-Street Lot"),
    NO_STANDING_TRUCK_LOADING("NO STANDING-EXC. TRUCK LOADING", "No Standing - Truck Loading"),
    NO_STANDING_AUTH_VEHICLE("NO STANDING-EXC. AUTH. VEHICLE", "No Standing - Authorized Vehicles Only"),
    NO_STANDING_BUS_LANE("NO STANDING-BUS LANE", "No Standing - Bus Lane"),
    NO_STANDING_BUS_STOP("NO STANDING-BUS STOP", "No Standing - Bus Stop"),
    NO_PARKING_DAY_TIME("NO PARKING-DAY/TIME LIMITS", "No Parking Zone"),
    NO_PARKING_STREET_CLEANING("NO PARKING-STREET CLEANING", "No Parking - Street Cleaning"),
    NO_STAND_TAXI_RELIEF("NO STAND TAXI/FHV RELIEF STAND", "No Standing - Taxi Relief Stand"),
    NO_PARKING_TAXI_STAND("NO PARKING-TAXI STAND", "No Parking - Taxi Stand"),
    NO_PARKING_AUTH_VEHICLE("NO PARKING-EXC. AUTH. VEHICLE", "No Parking - Authorized Vehicles Only"),
    NO_STANDING_COMMUTER_VAN("NO STANDING-COMMUTER VAN STOP", "No Standing - Commuter Van Stop"),
    NO_STANDING_FOR_HIRE("NO STANDING-FOR HIRE VEH STND", "No Standing - For-Hire Vehicle"),
    NO_PARKING_DISABILITY("NO PARKING-EXC. DSBLTY PERMIT", "No Parking - Disability Permit Only"),
    OVERTIME_STANDING_DP("OVERTIME STANDING DP", "Overtime Standing - Disability Zone"),
    ALTERING_BUS_PERMIT("ALTERING INTERCITY BUS PERMIT", "Altered Bus Permit"),
    NO_STOP_EXCEPT_PICKUP("NO STOP/STANDNG EXCEPT PAS P/U", "No Stopping Except Passenger Pick-Up"),
    NO_STANDING_COMM_METER("NO STANDING-COMM METER ZONE", "No Standing - Commercial Meter Zone"),
    OVERTIME_BROKEN_METER("OT PARKING-MISSING/BROKEN METR", "Overtime - Broken Meter"),
    MISUSE_PARKING_PERMIT("MISUSE PARKING PERMIT", "Parking Permit Misuse"),
    EXPIRED_METER("EXPIRED METER", "Expired Meter"),
    SELLING_AT_METER("SELLING/OFFERING MCHNDSE-METER", "Selling Merchandise at Meter"),
    SCHOOL_ZONE_SPEED("PHTO SCHOOL ZN SPEED VIOLATION", "School Zone Speed Violation"),
    EXPIRED_MUNI_METER("EXPIRED MUNI METER", "Expired Muni Meter"),
    FAIL_DISPLAY_MUNI_RECEIPT("FAIL TO DSPLY MUNI METER RECPT", "Muni Meter Receipt Not Displayed"),
    OVERTIME_TIME_LIMIT("OVERTIME PKG-TIME LIMIT POSTED", "Overtime Parking"),
    FIRE_HYDRANT("FIRE HYDRANT", "Fire Hydrant"),
    MISCELLANEOUS("MISCELLANEOUS", "Miscellaneous"),
    EXPIRED_MUNI_COMM_ZONE("EXPIRED MUNI MTR-COMM MTR ZN", "Expired Muni Meter - Commercial Zone"),
    EXPIRED_METER_COMM_ZONE("EXPIRED METER-COMM METER ZONE", "Expired Meter - Commercial Zone"),
    OVERTIME_COMM_METER("PKG IN EXC. OF LIM-COMM MTR ZN", "Overtime - Commercial Meter Zone"),
    TRAFFIC_LANE("TRAFFIC LANE", "Parked in Traffic Lane"),
    DOUBLE_PARKING("DOUBLE PARKING", "Double Parking"),
    DOUBLE_PARKING_MIDTOWN("DOUBLE PARKING-MIDTOWN COMML", "Double Parking - Midtown Commercial"),
    BIKE_LANE("BIKE LANE", "Bike Lane Violation"),
    EXCAVATION_OBSTRUCTION("EXCAVATION-VEHICLE OBSTR TRAFF", "Excavation Zone Obstruction"),
    CROSSWALK("CROSSWALK", "Parked in Crosswalk"),
    SIDEWALK("SIDEWALK", "Parked on Sidewalk"),
    INTERSECTION("INTERSECTION", "Parked in Intersection"),
    SAFETY_ZONE("SAFETY ZONE", "Parked in Safety Zone"),
    PICKUP_DISCHARGE_PROHIBITED("PCKP DSCHRGE IN PRHBTD ZONE", "Pick-Up/Discharge in Prohibited Zone"),
    ELEVATED_HIGHWAY("ELEVATED/DIVIDED HIGHWAY/TUNNL", "Elevated/Divided Highway"),
    DIVIDED_HIGHWAY("DIVIDED HIGHWAY", "Divided Highway"),
    WEIGH_IN_MOTION("WEIGH IN MOTION VIOLATION", "Weigh-in-Motion Violation"),
    MARGINAL_STREET("MARGINAL STREET/WATER FRONT", "Waterfront/Marginal Street"),
    ANGLE_PARKING_COMM("ANGLE PARKING-COMM VEHICLE", "Angle Parking - Commercial"),
    ANGLE_PARKING("ANGLE PARKING", "Angle Parking"),
    WRONG_WAY("WRONG WAY", "Wrong Way"),
    BEYOND_MARKED_SPACE("BEYOND MARKED SPACE", "Beyond Marked Space"),
    NIGHTTIME_PARK("NIGHTTIME STD/ PKG IN A PARK", "Nighttime Parking in Park"),
    NO_STANDING_EXCEPT_DS("NO STANDING EXCP D/S", "No Standing Except Diplomat"),
    OVERTIME_STANDING_DS("OVERTIME STDG D/S", "Overtime Standing - Diplomat"),
    DETACHED_TRAILER("DETACHED TRAILER", "Detached Trailer"),
    PEDESTRIAN_RAMP("PEDESTRIAN RAMP", "Blocking Pedestrian Ramp"),
    NON_COMPLIANCE_SIGN("NON-COMPLIANCE W/ POSTED SIGN", "Non-Compliance with Posted Sign"),
    FAIL_DISP_MUNI_RECEIPT("FAIL TO DISP. MUNI METER RECPT", "Muni Meter Receipt Not Displayed"),
    REG_STICKER_EXPIRED("REG. STICKER-EXPIRED/MISSING", "Registration Sticker Expired/Missing"),
    INSP_STICKER_EXPIRED("INSP. STICKER-EXPIRED/MISSING", "Inspection Sticker Expired/Missing"),
    INSP_STICKER_MUTILATED("INSP STICKER-MUTILATED/C'FEIT", "Inspection Sticker Mutilated/Counterfeit"),
    REG_STICKER_MUTILATED("REG STICKER-MUTILATED/C'FEIT", "Registration Sticker Mutilated/Counterfeit"),
    PLATE_MISSING("FRONT OR BACK PLATE MISSING", "License Plate Missing"),
    NO_MATCH_PLATE_STICKER("NO MATCH-PLATE/STICKER", "Plate/Sticker Mismatch"),
    VIN_OBSCURED("VIN OBSCURED", "VIN Obscured"),
    PARKED_BUS_WRONG_AREA("PARKED BUS-EXC. DESIG. AREA", "Bus Outside Designated Area"),
    NIGHT_PARKING_COMM_VEH("NGHT PKG ON RESID STR-COMM VEH", "Commercial Vehicle Night Parking"),
    UNAUTHORIZED_BUS_LAYOVER("UNAUTHORIZED BUS LAYOVER", "Unauthorized Bus Layover"),
    MISSING_EQUIPMENT("MISSING EQUIPMENT", "Missing Equipment"),
    NO_STANDING_EXCEPT_DP("NO STANDING EXCP DP", "No Standing Except Disability"),
    COMM_PLATES_UNALTERED("COMML PLATES-UNALTERED VEHICLE", "Commercial Plates on Unaltered Vehicle"),
    IMPROPER_REGISTRATION("IMPROPER REGISTRATION", "Improper Registration"),
    PLATFORM_LIFTS("PLTFRM LFTS LWRD POS COMM VEH", "Platform Lifts Position Violation"),
    STORAGE_3HR_COMMERCIAL("STORAGE-3HR COMMERCIAL", "Commercial Storage - 3 Hour Limit"),
    MIDTOWN_3HR_LIMIT("MIDTOWN PKG OR STD-3HR LIMIT", "Midtown 3 Hour Limit"),
    FRAUDULENT_PARKING_PERMIT("FRAUDULENT USE PARKING PERMIT", "Fraudulent Parking Permit"),
    UNALTERED_COMM_NO_INFO("UNALTERED COMM VEH-NME/ADDRESS", "Commercial Vehicle Missing Info"),
    NO_STAND_TRUCKS_GMTDST("NO STD(EXC TRKS/GMTDST NO-TRK)", "No Standing - Garment District"),
    VEHICLE_SALE_WASH_REPAIR("VEH-SALE/WSHNG/RPRNG/DRIVEWAY", "Vehicle Sale/Wash/Repair in Driveway"),
    VEHICLE_FOR_SALE("VEHICLE FOR SALE(DEALERS ONLY)", "Vehicle For Sale - Dealers Only"),
    WASH_REPAIR_ONLY("WASH/REPAIR VEHCL-REPAIR ONLY", "Vehicle Wash/Repair Zone"),
    FLAT_TIRE("REMOVE/REPLACE FLAT TIRE", "Changing Flat Tire"),
    RAILROAD_CROSSING("RAILROAD CROSSING", "Railroad Crossing"),
    VACANT_LOT("VACANT LOT", "Parked in Vacant Lot"),
    OBSTRUCTING_DRIVEWAY("OBSTRUCTING DRIVEWAY", "Obstructing Driveway"),
    OTHER("OTHER", "Other"),
    UNKNOWN("", "Unknown Violation");

    fun isCamera(): Boolean = this == SCHOOL_ZONE_SPEED || this == FAILURE_TO_STOP_RED_LIGHT

    companion object {
        private const val TAG = "ViolationType"

        fun fromCode(code: String?): ViolationType {
            if (code.isNullOrBlank()) return UNKNOWN
            val normalized = code.trim().uppercase()
            return entries.find { it.code.equals(normalized, ignoreCase = true) } ?: UNKNOWN
        }

        fun getDisplayName(code: String?): String {
            val type = fromCode(code)
            if (type == UNKNOWN && !code.isNullOrBlank()) {
                Log.w(TAG, "Unknown violation code: $code")
                return code
            }
            return type.displayName
        }
    }
}
