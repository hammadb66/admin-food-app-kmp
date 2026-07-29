package org.example.adminfoodapp.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DriverFactory actual constructor() {
    actual fun createDriver(): SqlDriver {
        val folder = File(System.getProperty("user.home"), "XCafe_Data")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val dbFile = File(folder, "restaurant.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

        if (!dbFile.exists() || dbFile.length() == 0L) {
            RestaurantDatabase.Schema.create(driver)
        }
        return driver
    }
}