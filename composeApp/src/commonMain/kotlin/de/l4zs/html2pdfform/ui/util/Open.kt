package de.l4zs.html2pdfform.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.failed_open
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import java.awt.Desktop
import java.io.File
import java.io.IOException

/**
 * Opens the given file with the default application.
 *
 * @param text The text to display.
 * @param file The file to open.
 * @param logger The logger to log errors to.
 * @param color The color of the text.
 */
@Composable
fun Open(
    text: String,
    file: File,
    logger: Logger,
    color: Color = MaterialTheme.colors.primary,
) {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    Text(
        text,
        Modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable {
                if (desktop?.isSupported(Desktop.Action.OPEN) == true) {
                    try {
                        desktop.open(file)
                    } catch (e: IOException) {
                        runBlocking {
                            logger.error(getString(Res.string.failed_open), e)
                        }
                    }
                }
            },
        color = color,
    )
}
