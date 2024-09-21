package de.l4zs.html2pdfform.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.converter.Converter
import de.l4zs.html2pdfform.ui.Page
import de.l4zs.html2pdfform.ui.viewmodel.GeneratorViewModel
import de.l4zs.html2pdfform.util.Logger
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import java.awt.SystemColor.text
import java.nio.charset.Charset

@Composable
fun GeneratorPage(
    navController: NavController,
    logger: Logger,
    converter: Converter,
    config: Config,
) {
    val viewModel = viewModel { GeneratorViewModel(logger, converter, config) }

    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        Navbar(navController)

        Spacer(modifier = Modifier.height(16.dp))

        UrlInput(viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        FileSelect(viewModel, logger)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        GeneratePdfButton(viewModel, logger)
    }
}

@Composable
private fun Navbar(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "PDF Formular Generator",
            style = MaterialTheme.typography.h6,
        )
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { navController.navigate(Page.HELP.route) },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            ) {
                Icon(Icons.Default.Info, contentDescription = "Hilfe")
            }
            IconButton(
                onClick = { navController.navigate(Page.SETTINGS.route) },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Einstellungen")
            }
        }
    }
}

@Composable
private fun UrlInput(viewModel: GeneratorViewModel) {
    val url by viewModel.url.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    OutlinedTextField(
        value = url,
        onValueChange = { viewModel.updateUrl(it) },
        label = { Text("URL") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        trailingIcon = {
            TextButton(
                onClick = {
                    if (url.isBlank()) {
                        return@TextButton
                    }
                    viewModel.loadUrl()
                },
                modifier =
                    Modifier
                        .padding(8.dp)
                        .pointerHoverIcon(PointerIcon.Hand),
                enabled = !isLoading,
            ) {
                Text(if (isLoading) "Lädt..." else "URL laden")
            }
        },
    )
}

@Composable
private fun FileSelect(
    viewModel: GeneratorViewModel,
    logger: Logger,
) {
    val fileName by viewModel.fileName.collectAsState()

    val filePicker =
        rememberFilePickerLauncher(
            type = PickerType.File(listOf("html")),
            mode = PickerMode.Single,
            title = "Wähle eine HTML-Datei aus",
        ) { file ->
            if (file == null) {
                return@rememberFilePickerLauncher
            }
            try {
                viewModel.updateText(file.file.readText(Charset.defaultCharset()))
                logger.success("Datei erfolgreich geladen")
            } catch (e: Exception) {
                logger.warn("Fehler beim Laden der Datei", e)
            }
            viewModel.updateFileName(file.file.path)
        }

    OutlinedTextField(
        value = fileName,
        onValueChange = { /* Read-only */ },
        label = { Text("Datei") },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        singleLine = true,
        trailingIcon = {
            TextButton(
                onClick = { filePicker.launch() },
                modifier =
                    Modifier
                        .padding(8.dp)
                        .pointerHoverIcon(PointerIcon.Hand),
            ) {
                Text("Datei auswählen")
            }
        },
    )
}

@Composable
private fun TextField(viewModel: GeneratorViewModel) {
    val text by viewModel.text.collectAsState()

    OutlinedTextField(
        value = text,
        onValueChange = { viewModel.updateText(it) },
        label = { Text("Text") },
        modifier = Modifier.fillMaxSize(),
        minLines = 1,
        maxLines = 10,
    )
}

@Composable
private fun GeneratePdfButton(
    viewModel: GeneratorViewModel,
    logger: Logger,
) {
    val isGenerating by viewModel.isGenerating.collectAsState()
    val text by viewModel.text.collectAsState()

    val fileSaver =
        rememberFileSaverLauncher {
            if (it == null) {
                return@rememberFileSaverLauncher
            }
            logger.success("PDF-Datei erfolgreich gespeichert")
        }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
    ) {
        Button(
            onClick = {
                if (text.isBlank()) {
                    return@Button
                }
                val pdf = viewModel.generatePDF()
                println(pdf?.size)
                if (pdf != null) {
                    fileSaver.launch(
                        baseName = "Formular",
                        extension = "pdf",
                        bytes = pdf,
                    )
                }
            },
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            enabled = text.isNotBlank() && !isGenerating,
        ) {
            Text(
                if (isGenerating) "PDF wird erstellt..." else "PDF erstellen",
            )
        }
    }
}
