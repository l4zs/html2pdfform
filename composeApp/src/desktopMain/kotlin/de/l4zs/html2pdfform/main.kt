package de.l4zs.html2pdfform

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import de.l4zs.html2pdfform.backend.config.ConfigContext
import de.l4zs.html2pdfform.backend.config.loadConfigFromFile
import de.l4zs.html2pdfform.backend.data.toLocale
import de.l4zs.html2pdfform.backend.converter.createConverter
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.app_name
import de.l4zs.html2pdfform.resources.icon
import de.l4zs.html2pdfform.ui.PDFFormGeneratorApp
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.util.*

fun main() =
    application {
        val logger = Logger()

        val config =
            runBlocking {
                loadConfigFromFile(logger)
            }
        logger.logLevel = config.logLevel
        val configContext = ConfigContext(config)
        val converter = createConverter(logger, configContext)

        val windowState =
            rememberWindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = DpSize(920.dp, 690.dp),
            )

        val icon = painterResource(Res.drawable.icon)

        Locale.setDefault(config.language.toLocale())

        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.app_name),
            state = windowState,
            icon = icon,
        ) {
            PDFFormGeneratorApp(logger, converter, configContext)
        }
    }
