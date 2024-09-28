package de.l4zs.html2pdfform.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import java.awt.Desktop
import java.io.File

@Composable
fun Open(
    text: String,
    file: File,
    color: Color = MaterialTheme.colors.primary,
) {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    Text(
        text,
        Modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable {
                if (desktop?.isSupported(Desktop.Action.OPEN) == true) {
                    desktop.open(file)
                }
            },
        color = color,
    )
}
