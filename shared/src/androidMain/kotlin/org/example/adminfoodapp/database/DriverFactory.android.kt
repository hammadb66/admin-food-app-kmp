package org.example.adminfoodapp.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.core.context.GlobalContext

actual class DriverFactory actual constructor() {
    actual fun createDriver(): SqlDriver {
        // Koin se automatically Android Application Context milega
        val context: Context = GlobalContext.get().get()
        return AndroidSqliteDriver(RestaurantDatabase.Schema, context, "restaurant.db")
    }
}