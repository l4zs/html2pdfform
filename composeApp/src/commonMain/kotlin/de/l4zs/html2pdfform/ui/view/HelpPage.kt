package de.l4zs.html2pdfform.ui.view

import androidx.compose.foundation.layout.*
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
import de.l4zs.html2pdfform.util.Logger

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
        // Add more info content here
    }
}
