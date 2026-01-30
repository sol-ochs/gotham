package com.aurox.gotham.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class VehicleWithTickets(
    @Embedded
    val vehicle: VehicleEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "vehicle_id"
    )
    val tickets: List<TicketEntity>
)
