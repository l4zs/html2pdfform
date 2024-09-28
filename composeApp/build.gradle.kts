import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kotlin.power.assert)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting
        val desktopTest by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.preview)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.vinceglb.filekit.compose)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.serialization.json)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(compose.components.resources)

            implementation(libs.librepdf.openpdf)
            implementation(libs.jsoup.jsoup)
            implementation(libs.kotlinx.serialization.json)
        }
        desktopTest.dependencies {
            implementation(libs.kotlin.test)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
    }
}

compose.desktop {
    application {
        mainClass = "de.l4zs.html2pdfform.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Deb,
            )
            packageName = "html2pdfform"
            packageVersion = "1.0.0"
            description = "HTML to PDF Form Converter"
            copyright = "© 2024 Jannis Kramer. All rights reserved."
            vendor = "Jannis Kramer"
            licenseFile.set(file("../LICENSE"))

            windows {
                shortcut = true
                dirChooser = true
                perUserInstall = true
                iconFile.set(file("../files/icon.ico"))
            }

            macOS {
                iconFile.set(file("../files/icon.icns"))
            }

            linux {
                shortcut = true
                iconFile.set(file("../files/icon.png"))
            }
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "de.l4zs.html2pdfform.resources"
    generateResClass = auto
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
powerAssert {
    functions =
        listOf(
            "kotlin.assert",
            "kotlin.test.assertTrue",
            "kotlin.test.assertEquals",
            "kotlin.test.assertNull",
            "kotlin.require",
        )
}
