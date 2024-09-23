package de.l4zs.html2pdfform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import de.l4zs.html2pdfform.backend.config.ConfigContext
import de.l4zs.html2pdfform.backend.config.loadConfigFromFile
import de.l4zs.html2pdfform.backend.converter.createConverter
import de.l4zs.html2pdfform.ui.PDFFormGeneratorApp
import de.l4zs.html2pdfform.util.Logger
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToSvgPainter

fun main() =
    application {
        val logger = Logger()
        val config = loadConfigFromFile(logger)
        val configContext = ConfigContext(config)
        val converter = createConverter(logger, configContext)

        val windowState =
            rememberWindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = DpSize(920.dp, 690.dp),
            )

        Window(
            onCloseRequest = ::exitApplication,
            title = "html2pdfform",
            state = windowState,
            icon = rememberSvgResource("icon.svg"),
        ) {
            PDFFormGeneratorApp(logger, converter, configContext)
        }
    }

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun rememberSvgResource(path: String): Painter {
    val density = LocalDensity.current
    return remember(density, path) { readResourceBytes(path).decodeToSvgPainter(density) }
}

private object ResourceLoader

private fun readResourceBytes(resourcePath: String) =
    ResourceLoader.javaClass.classLoader
        .getResourceAsStream(resourcePath)
        .readAllBytes()
