import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"

}

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
    implementation(libs.koin.core)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.firebase.app)   // ← ye add karo

}

compose.desktop {
    application {
        mainClass = "org.example.adminfoodapp.MainKt"
        jvmArgs += listOf("-Dskiko.renderApi=OPENGL")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AdminFoodApp"
            packageVersion = "1.0.0"
            vendor = "Zavion Technologies"
            description = "Restaurant Order Management App"
            includeAllModules = true
            windows {
                shortcut=true
                menuGroup = "AdminFoodApp"
                upgradeUuid = "b7e27137-4a66-4142-9dd6-bd8ebe0378f5"
//                 iconFile.set(project.file("icon.ico"))
            }
        }
    }
}
