package de.l4zs.html2pdfform.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.l4zs.html2pdfform.converter.HtmlConverter
import de.l4zs.html2pdfform.ui.setting.SettingsPage
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URI
import java.nio.charset.Charset


class PDFFormViewModel : ViewModel() {
    private val _url = MutableStateFlow("")
    val url = _url.asStateFlow()

    private val _fileName = MutableStateFlow("")
    val fileName = _fileName.asStateFlow()

    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    fun updateUrl(newUrl: String) {
        _url.value = newUrl
    }

    fun updateFileName(newFileName: String) {
        _fileName.value = newFileName
    }

    fun updateText(newText: String) {
        _text.value = newText
    }

    fun setGenerating(newValue: Boolean) {
        _isGenerating.value = newValue
    }

    fun loadUrl() {
        if (_url.value.isNotBlank()) {
            _isLoading.value = true
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val content = URI(_url.value).toURL().readText()
                    _text.value = content
                } catch (e: Exception) {
                    _text.value = "Fehler beim Laden der URL: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
}

@Composable
fun PDFFormGeneratorApp() {
    val navController = rememberNavController()
    val viewModel: PDFFormViewModel = viewModel()

    NavHost(
        navController,
        startDestination = "main",
        enterTransition = {
            fadeIn(tween(250))
        },
        exitTransition = { ExitTransition.None }
    ) {
        composable("main") { PDFFormGenerator(navController, viewModel) }
        composable("help") { HelpPage(navController) }
        composable("settings") { SettingsPage(navController) }
    }
}

@Composable
fun PDFFormGenerator(navController: androidx.navigation.NavController, viewModel: PDFFormViewModel) {
    val url by viewModel.url.collectAsState()
    val fileName by viewModel.fileName.collectAsState()
    val text by viewModel.text.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()

    val filePicker = rememberFilePickerLauncher(
        type = PickerType.File(listOf("html")),
        mode = PickerMode.Single,
        title = "Wähle eine HTML-Datei aus",
        initialDirectory = null
    ) { file ->
        if (file != null) {
            try {
                viewModel.updateText(file.file.readText(Charset.defaultCharset()))
            } catch (e: Exception) {
                // TODO: Show error message
            }
            viewModel.updateFileName(file.file.path)
        } else {
            viewModel.updateFileName("")
        }
    }

    val fileSaver = rememberFileSaverLauncher {
        if (it != null) {
            // TODO: Show success message
        }
        viewModel.setGenerating(false)
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PDF Formular Generator",
                        style = MaterialTheme.typography.h6
                    )
                    Row {
                        IconButton(
                            onClick = { navController.navigate("help") },
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "Hilfe")
                        }
                        IconButton(
                            onClick = { navController.navigate("settings") },
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Einstellungen")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { viewModel.updateUrl(it) },
                    label = { Text("URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        TextButton(
                            onClick = { viewModel.loadUrl() },
                            modifier = Modifier
                                .padding(8.dp)
                                .pointerHoverIcon(PointerIcon.Hand),
                            enabled = !isLoading
                        ) {
                            Text(if (isLoading) "Lädt..." else "URL laden")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                            modifier = Modifier
                                .padding(8.dp)
                                .pointerHoverIcon(PointerIcon.Hand)
                        ) {
                            Text("Datei auswählen")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { viewModel.updateText(it) },
                    label = { Text("Text") },
                    modifier = Modifier.fillMaxSize(),
                    minLines = 1,
                    maxLines = 10,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.setGenerating(true)
                        val pdf =try {
                            HtmlConverter().convert(text)!!
                        } catch (e: Exception) {
                            // TODO: Show error message(s)
                            null
                        }
                        if (pdf != null) {
                            fileSaver.launch(
                                baseName = "Formular",
                                extension = "pdf",
                                bytes = pdf
                            )
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                        .pointerHoverIcon(PointerIcon.Hand),
                    enabled = text.isNotBlank() && !isGenerating
                ) {
                    Text(
                        if (isGenerating) "PDF wird erstellt..." else "PDF erstellen"
                    )
                }
            }
        }
    }
}
