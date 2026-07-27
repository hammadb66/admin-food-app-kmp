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
        jvmArgs += listOf("-Dskiko.renderApi=OPENGL")   // ya "SOFTWARE" try karo agar OPENGL na chale

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.adminfoodapp"
            packageVersion = "1.0.0"
        }
    }
}
