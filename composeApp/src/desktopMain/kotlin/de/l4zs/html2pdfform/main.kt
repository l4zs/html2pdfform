package de.l4zs.html2pdfform

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.l4zs.html2pdfform.config.Config
import de.l4zs.html2pdfform.config.loadConfig
import de.l4zs.html2pdfform.ui.PDFFormGeneratorApp
import kotlinx.serialization.json.Json
import java.io.File

fun main() = application {
    Config.loadConfig()

    Window(
        onCloseRequest = ::exitApplication,
        title = "html2pdfform",
    ) {
        PDFFormGeneratorApp()
    }
}
