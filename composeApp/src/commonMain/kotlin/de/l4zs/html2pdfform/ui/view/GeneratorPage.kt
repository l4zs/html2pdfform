package de.l4zs.html2pdfform.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.converter.Converter
import de.l4zs.html2pdfform.resources.*
import de.l4zs.html2pdfform.ui.Page
import de.l4zs.html2pdfform.ui.util.DragDropTargetOverlay
import de.l4zs.html2pdfform.ui.viewmodel.GeneratorViewModel
import de.l4zs.html2pdfform.util.Logger
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import java.io.File

/**
 * The generator page.
 * This page is the main page of the application.
 * It allows the user to generate a PDF from a URL, a file, or a text.
 *
 * @param navController The navigation controller to use for navigating between pages.
 * @param logger The logger to use for logging messages.
 * @param converter The converter to use for generating the PDF.
 * @param config The configuration.
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun GeneratorPage(
    navController: NavController,
    logger: Logger,
    converter: Converter,
    config: Config,
) {
    val viewModel = viewModel { GeneratorViewModel(logger, converter, config) }
    var isDragging by remember { mutableStateOf(false) }

    val dragAndDropCallback =
        remember {
            object : DragAndDropTarget {
                override fun onDrop(event: DragAndDropEvent): Boolean {
                    val files = event.dragData() as? DragData.FilesList
                    val path = files?.readFiles()?.firstOrNull() ?: return false
                    val file = File(path.substringAfter("file:/"))
                    viewModel.loadFile(file)
                    return true
                }

                override fun onEnded(event: DragAndDropEvent) {
                    isDragging = false
                }
            }
        }

    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        Navbar(navController)

        Spacer(modifier = Modifier.height(16.dp))

        DragDropTargetOverlay(
            isDragging,
            Modifier.dragAndDropTarget({ event ->
                val files =
                    event.dragData() as? DragData.FilesList ?: run {
                        isDragging = false
                        return@dragAndDropTarget false
                    }
                val start = files.readFiles().firstOrNull()?.startsWith("file:/") == true
                isDragging = start
                return@dragAndDropTarget start
            }, dragAndDropCallback),
        ) {
            Column {
                UrlInput(viewModel)

                Spacer(modifier = Modifier.height(16.dp))

                FileSelect(viewModel)

                Spacer(modifier = Modifier.height(16.dp))

                TextField(viewModel)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        GeneratePdfButton(viewModel, logger)
    }
}

/**
 * The navigation bar.
 *
 * @param navController The navigation controller to use for navigating between pages.
 */
@Composable
private fun Navbar(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.generator_page_title),
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
                Icon(Icons.Default.Info, stringResource(Res.string.help_page_name))
            }
            IconButton(
                onClick = { navController.navigate(Page.SETTINGS.route) },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            ) {
                Icon(Icons.Default.Settings, stringResource(Res.string.settings_page_name))
            }
        }
    }
}

/**
 * The URL input field. This field allows the user to input a URL and to load the content of the URL into the text field.
 *
 * @param viewModel The view model to use for the URL input.
 */
@Composable
private fun UrlInput(viewModel: GeneratorViewModel) {
    val url by viewModel.url.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    OutlinedTextField(
        value = url,
        onValueChange = { viewModel.updateUrl(it) },
        label = { Text(stringResource(Res.string.generator_page_url_label)) },
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
                Text(
                    if (isLoading) {
                        stringResource(
                            Res.string.generator_page_url_text_loading,
                        )
                    } else {
                        stringResource(Res.string.generator_page_url_text)
                    },
                )
            }
        },
    )
}

/**
 * The file select field.
 * This field allows the user to select a file from the file system
 * and to load the content of the file into the text field.
 *
 * @param viewModel The view model to use for the file select.
 */
@Composable
private fun FileSelect(viewModel: GeneratorViewModel) {
    val fileName by viewModel.fileName.collectAsState()

    val filePicker =
        rememberFilePickerLauncher(
            type = PickerType.File(listOf("html")),
            mode = PickerMode.Single,
            title = stringResource(Res.string.generator_page_file_picker_title),
        ) { file ->
            if (file == null) {
                return@rememberFilePickerLauncher
            }
            viewModel.loadFile(file.file)
        }

    OutlinedTextField(
        value = fileName,
        onValueChange = { /* Read-only */ },
        label = { Text(stringResource(Res.string.generator_page_file_label)) },
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
                Text(stringResource(Res.string.generator_page_file_text))
            }
        },
    )
}

/**
 * The text field.
 * This field allows the user to input text that will be used to generate the PDF.
 * Alternatively, the user can paste text into the field or use a URL or a file to load the content.
 *
 * @param viewModel The view model to use for the text field.
 */
@Composable
private fun TextField(viewModel: GeneratorViewModel) {
    val text by viewModel.text.collectAsState()

    OutlinedTextField(
        value = text,
        onValueChange = { viewModel.updateText(it) },
        label = { Text(stringResource(Res.string.generator_page_text_text)) },
        modifier = Modifier.fillMaxSize(),
        minLines = 1,
        maxLines = 10,
    )
}

/**
 * The generate PDF button.
 * This button allows the user to generate a PDF from the text field and to save the PDF to the file system.
 *
 * @param viewModel The view model to use for the generate PDF button.
 * @param logger The logger to use for logging messages.
 */
@Composable
private fun GeneratePdfButton(
    viewModel: GeneratorViewModel,
    logger: Logger,
) {
    val isGenerating by viewModel.isGenerating.collectAsState()
    val text by viewModel.text.collectAsState()

    // translations for convenience to avoid blocking the UI thread
    val baseName = stringResource(Res.string.generator_page_pdf_basename)
    val saveSuccess = stringResource(Res.string.generator_page_pdf_save_success)
    val saveError = stringResource(Res.string.generator_page_pdf_save_error)

    val fileSaver =
        rememberFileSaverLauncher {
            if (it == null) {
                return@rememberFileSaverLauncher
            }
            logger.success(saveSuccess)
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
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    val pdf = viewModel.generatePDF()
                    withContext(Dispatchers.Main) {
                        if (pdf != null) {
                            try {
                                fileSaver.launch(
                                    baseName = baseName,
                                    extension = "pdf",
                                    bytes = pdf,
                                )
                            } catch (e: Exception) {
                                logger.warn(saveError, e)
                            }
                        }
                    }
                }
            },
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            enabled = text.isNotBlank() && !isGenerating,
        ) {
            Text(
                if (isGenerating) {
                    stringResource(
                        Res.string.generator_page_pdf_label_loading,
                    )
                } else {
                    stringResource(Res.string.generator_page_pdf_label)
                },
            )
        }
    }
}
