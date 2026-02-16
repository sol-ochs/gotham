package com.aurox.gotham.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS vehicles_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    license_plate TEXT NOT NULL,
                    state TEXT NOT NULL,
                    nickname TEXT,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO vehicles_new (
                    id, license_plate, state, nickname, created_at, updated_at
                )
                SELECT id, license_plate, state, nickname, created_at, updated_at
                FROM vehicles
                """.trimIndent()
            )
            db.execSQL("DROP TABLE vehicles")
            db.execSQL("ALTER TABLE vehicles_new RENAME TO vehicles")
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                UPDATE vehicles
                SET license_plate = UPPER(TRIM(license_plate)),
                    state = UPPER(TRIM(state))
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS vehicle_duplicate_map (
                    duplicate_id INTEGER PRIMARY KEY NOT NULL,
                    canonical_id INTEGER NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                INSERT INTO vehicle_duplicate_map (duplicate_id, canonical_id)
                SELECT v.id AS duplicate_id,
                       (
                           SELECT MIN(v2.id)
                           FROM vehicles v2
                           WHERE v2.license_plate = v.license_plate
                             AND v2.state = v.state
                       ) AS canonical_id
                FROM vehicles v
                WHERE v.id != (
                    SELECT MIN(v3.id)
                    FROM vehicles v3
                    WHERE v3.license_plate = v.license_plate
                      AND v3.state = v.state
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                UPDATE tickets
                SET vehicle_id = (
                    SELECT canonical_id
                    FROM vehicle_duplicate_map
                    WHERE duplicate_id = tickets.vehicle_id
                )
                WHERE vehicle_id IN (
                    SELECT duplicate_id FROM vehicle_duplicate_map
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                DELETE FROM vehicles
                WHERE id IN (
                    SELECT duplicate_id FROM vehicle_duplicate_map
                )
                """.trimIndent()
            )

            db.execSQL("DROP TABLE vehicle_duplicate_map")

            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS
                index_vehicles_license_plate_state
                ON vehicles(license_plate, state)
                """.trimIndent()
            )
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                ALTER TABLE tickets
                ADD COLUMN is_paid_override INTEGER NOT NULL DEFAULT 0
                """.trimIndent()
            )

            db.execSQL(
                """
                ALTER TABLE tickets
                ADD COLUMN paid_override_at INTEGER
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS deadline_reminder_events (
                    summons_number TEXT NOT NULL,
                    milestone_day INTEGER NOT NULL,
                    sent_at INTEGER NOT NULL,
                    PRIMARY KEY(summons_number, milestone_day)
                )
                """.trimIndent()
            )
        }
    }

    val ALL: Array<Migration> = arrayOf(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
}
