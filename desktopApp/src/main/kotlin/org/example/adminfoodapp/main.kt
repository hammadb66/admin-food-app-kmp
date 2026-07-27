package org.example.adminfoodapp

import android.app.Application
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import org.koin.core.context.startKoin
import org.example.adminfoodapp.di.appModules

fun main() = application {


    // 1) Storage/logging platform setup (JVM ke liye zaroori)
    FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
        val storage = mutableMapOf<String, String>()
        override fun store(key: String, value: String) = storage.set(key, value)
        override fun retrieve(key: String) = storage[key]
        override fun clear(key: String) {
            storage.remove(key)
        }

        override fun log(msg: String) = println(msg)
    })


    // 2) Firebase init with dummy Application context
    val options = FirebaseOptions(
        apiKey = "AIzaSyDM2eqWcACB7TG4QTLefLaYHPf7eSfzFNw",
        authDomain = "admin-food-order-92432.firebaseapp.com",
        databaseUrl = "https://admin-food-order-92432-default-rtdb.firebaseio.com",
        projectId = "admin-food-order-92432",
        storageBucket = "admin-food-order-92432.appspot.com",
        applicationId = "1:294226982300:web:31f2cf630b3f337f618459"
    )


    Firebase.initialize(Application(), options)

    startKoin {
        modules(appModules)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Admin Food App",
    ) {
        App()
    }
}