package org.example.adminfoodapp.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database
import org.koin.dsl.module

val dataModule = module {
    single { Firebase.database.reference()   }
}