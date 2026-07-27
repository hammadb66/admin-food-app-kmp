import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)   // ✅ ye line add karo

}

kotlin {
    jvm()
    
    androidTarget {
       compilerOptions {
           jvmTarget.set(JvmTarget.JVM_11)
       }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            
            // Firebase Multiplatform
            implementation(libs.firebase.auth)
            implementation(libs.firebase.database)   // Firestore ki jagah ye

            //for koin dependency injection
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            //for compose navigation
            implementation(libs.navigation.compose)

            //For Serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

            //coil
            implementation("io.coil-kt.coil3:coil-compose:3.0.4")

            //foricons
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")


            //for ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)          // CIO engine — Android + Desktop dono pe chalta hai
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)



            implementation(libs.multiplatform.settings)



        }
        
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.koin.android)

            // Firebase Android Native (Correct way to use BoM in KMP androidMain)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.auth.android)



            implementation(libs.multiplatform.settings.no.arg)

        }
    }
}

android {
    namespace = "org.example.adminfoodapp.shared"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
