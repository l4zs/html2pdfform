package de.l4zs.html2pdfform

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.l4zs.html2pdfform.config.Config
import de.l4zs.html2pdfform.config.loadConfig
import de.l4zs.html2pdfform.ui.DesktopLogger
import de.l4zs.html2pdfform.ui.PDFFormGeneratorApp

fun main() =
    application {
        val logger = DesktopLogger()
        Config.loadConfig(logger)

        Window(
            onCloseRequest = ::exitApplication,
            title = "html2pdfform",
        ) {
            PDFFormGeneratorApp(logger)
        }
    }
