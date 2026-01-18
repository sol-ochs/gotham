package com.gotham.app.data.remote

import com.gotham.app.data.remote.dto.TicketDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NycOpenDataApi {
    @GET("nc67-uf89.json")
    suspend fun getTickets(
        @Query("\$where") whereClause: String,
        @Query("\$limit") limit: Int = 1000,
        @Query("\$offset") offset: Int = 0,
        @Query("\$order") order: String = "issue_date DESC"
    ): List<TicketDto>

    companion object {
        const val BASE_URL = "https://data.cityofnewyork.us/resource/"
        const val APP_TOKEN_HEADER = "X-App-Token"

        fun buildWhereClause(plate: String, state: String): String {
            val normalizedPlate = plate.uppercase().trim()
            val normalizedState = state.uppercase().trim()
            return "plate='$normalizedPlate' AND state='$normalizedState'"
        }

        fun buildWhereClauseForMultiplePlates(plates: List<Pair<String, String>>): String {
            if (plates.isEmpty()) return ""

            val conditions = plates.map { (plate, state) ->
                val normalizedPlate = plate.uppercase().trim()
                val normalizedState = state.uppercase().trim()
                "(plate='$normalizedPlate' AND state='$normalizedState')"
            }

            return conditions.joinToString(" OR ")
        }
    }
}
