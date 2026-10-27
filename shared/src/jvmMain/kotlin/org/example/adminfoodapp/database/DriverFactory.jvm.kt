package org.example.adminfoodapp.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DriverFactory actual constructor() {
    actual fun createDriver(): SqlDriver {
        val folder = resolveDataFolder()
        if (!folder.exists()) folder.mkdirs()

        val dbFile = File(folder, "restaurant.db")

        // 🔄 Migration: agar portable khaali hai lekin home mein purana data hai
        migrateFromHomeIfNeeded(dbFile)

        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

        if (!dbFile.exists() || dbFile.length() == 0L) {
            RestaurantDatabase.Schema.create(driver)
        }
        return driver
    }

    // Resolve where to put the DB
    private fun resolveDataFolder(): File {
        // 1. Portable location: <working dir>/data
        //    (user.dir = folder jahan se exe launch hua — D:\App\ → D:\App\data)
        val cwd = try {
            File(System.getProperty("user.dir") ?: ".")
        } catch (e: Exception) {
            null
        }

        val portable = cwd?.let { File(it, "data") }

        if (portable != null && isWritable(portable)) {
            return portable
        }

        // 2. Fallback: user.home/XCafe_Data (purana behavior — C:\Users\...\XCafe_Data)
        val home = File(System.getProperty("user.home") ?: ".", "XCafe_Data")
        if (!home.exists()) home.mkdirs()
        return home
    }

    private fun isWritable(dir: File): Boolean {
        return try {
            if (!dir.exists()) dir.mkdirs()
            val test = File(dir, ".write_test")
            test.writeText("test")
            test.delete()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Agar portable DB nahi hai, lekin home mein purana DB hai → copy kar do
    private fun migrateFromHomeIfNeeded(targetDb: File) {
        if (targetDb.exists() && targetDb.length() > 0) return

        val homeDb = File(
            File(System.getProperty("user.home") ?: ".", "XCafe_Data"),
            "restaurant.db"
        )

        if (homeDb.exists() &&
            homeDb.length() > 0 &&
            homeDb.absolutePath != targetDb.absolutePath) {
            try {
                homeDb.copyTo(targetDb, overwrite = false)
                println("✅ Migrated DB: ${homeDb.absolutePath} → ${targetDb.absolutePath}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}