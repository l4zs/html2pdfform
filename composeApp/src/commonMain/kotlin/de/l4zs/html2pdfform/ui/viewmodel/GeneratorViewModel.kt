package de.l4zs.html2pdfform.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.converter.Converter
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.net.URI
import java.nio.charset.Charset

class GeneratorViewModel(
    private val logger: Logger,
    private val converter: Converter,
    private val config: Config,
) : ViewModel() {
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

    fun loadUrl() {
        require(_url.value.isNotBlank()) { "URL darf nicht leer sein" }
        require(_isLoading.value.not()) { "URL wird bereits geladen" }
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO: get with http client
                val content = URI(_url.value).toURL().readText()
                _text.value = content
                logger.success("URL erfolgreich geladen")
            } catch (e: Exception) {
                // IOException, URISyntaxException, MalformedURLException
                logger.warn("Fehler beim Laden der URL", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadFile(file: File) {
        try {
            _text.value = file.readText(Charset.defaultCharset())
            logger.success("Datei erfolgreich geladen")
        } catch (e: IOException) {
            logger.warn("Fehler beim Laden der Datei", e)
        }
        _fileName.value = file.absolutePath
    }

    fun generatePDF(): ByteArray? {
        require(_text.value.isNotBlank()) { "Text darf nicht leer sein" }
        require(_isGenerating.value.not()) { "PDF wird bereits generiert" }
        _isGenerating.value = true
        try {
            return converter.convert(_text.value).also {
                logger.success("PDF erfolgreich generiert")
            }
        } catch (e: Exception) {
            // prevent crash if unknown error occurs
            logger.warn("Fehler beim Generieren der PDF", e)
            return null
        } finally {
            _isGenerating.value = false
        }
    }
}
