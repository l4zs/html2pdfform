package de.l4zs.html2pdfform.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.config.configFile
import de.l4zs.html2pdfform.ui.util.ExpandableSection
import de.l4zs.html2pdfform.util.Logger
import java.awt.Desktop
import java.io.IOException

@Composable
fun HelpPage(
    navController: NavController,
    logger: Logger,
    config: Config,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
            }
            Text("Hilfe", style = MaterialTheme.typography.h6)
            Spacer(Modifier.width(48.dp)) // For alignment
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Hier finden Sie Informationen zur Benutzung des PDF Formular Generators.")

        Spacer(modifier = Modifier.height(16.dp))

        WhereConfigLocated(logger)
    }
}

@Composable
private fun WhereConfigLocated(logger: Logger) {
    ExpandableSection("Wo ist die Konfigurationsdatei gespeichert?") {
        val configFile = configFile()
        val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
        Text("Die Konfigurationsdatei wird im Home-Verzeichnis des Benutzers gespeichert.")
        Row {
            Text("Der Pfad zur Konfigurationsdatei lautet: ")
            Text(
                configFile.absolutePath,
                Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
                    .clickable {
                        if (desktop == null) {
                            logger.warn("Diese Aktion ist nicht unterstützt", Error("Desktop API nicht verfügbar"))
                        }
                        try {
                            desktop?.open(configFile.parentFile)
                        } catch (exception: IOException) {
                            logger.error("Fehler beim Öffnen der Konfigurationsdatei", exception)
                        }
                    },
                MaterialTheme.colors.primary,
            )
        }
    }
}
