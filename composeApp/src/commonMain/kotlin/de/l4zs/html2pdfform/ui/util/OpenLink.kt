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
import de.l4zs.html2pdfform.resources.failed_browse
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import java.awt.Desktop

/**
 * Opens a link in the default browser.
 *
 * @param text The text to display.
 * @param url The URL to open.
 * @param logger The logger to log errors to.
 * @param color The color of the text.
 */
@Composable
fun OpenLink(
    text: String,
    url: String,
    logger: Logger,
    color: Color = MaterialTheme.colors.primary,
) {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    Text(
        text,
        Modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable {
                if (desktop?.isSupported(Desktop.Action.BROWSE) == true) {
                    try {
                        desktop.browse(java.net.URI(url))
                    } catch (e: java.io.IOException) {
                        runBlocking {
                            logger.error(getString(Res.string.failed_browse), e)
                        }
                    } catch (e: java.net.URISyntaxException) {
                        runBlocking {
                            logger.error(getString(Res.string.failed_browse), e)
                        }
                    }
                }
            },
        color = color,
    )
}
