package de.l4zs.html2pdfform

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import de.l4zs.html2pdfform.backend.config.ConfigContext
import de.l4zs.html2pdfform.backend.config.loadConfigFromFile
import de.l4zs.html2pdfform.backend.converter.createConverter
import de.l4zs.html2pdfform.ui.PDFFormGeneratorApp
import de.l4zs.html2pdfform.util.Logger

fun main() =
    application {
        val logger = Logger()
        val config = loadConfigFromFile(logger)
        val configContext = ConfigContext(config)
        val converter = createConverter(logger, configContext)

        val windowState =
            rememberWindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = DpSize(1200.dp, 900.dp),
            )

        Window(
            onCloseRequest = ::exitApplication,
            title = "html2pdfform",
            state = windowState,
        ) {
            PDFFormGeneratorApp(logger, converter, configContext)
        }
    }
