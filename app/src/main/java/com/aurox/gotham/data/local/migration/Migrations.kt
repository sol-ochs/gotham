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

    val ALL: Array<Migration> = arrayOf(MIGRATION_3_4)
}
