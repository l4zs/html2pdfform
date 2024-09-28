package de.l4zs.html2pdfform.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.converter.Converter
import de.l4zs.html2pdfform.resources.*
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.generator_view_model_load_url_error
import de.l4zs.html2pdfform.resources.generator_view_model_load_url_success
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
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
        require(_url.value.isNotBlank()) { "url cannot be empty" }
        require(_isLoading.value.not()) { "url is already loading" }
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO: get with http client
                val content = URI(_url.value).toURL().readText()
                _text.value = content
                logger.success(getString(Res.string.generator_view_model_load_url_success))
            } catch (e: Exception) {
                // IOException, URISyntaxException, MalformedURLException
                logger.error(getString(Res.string.generator_view_model_load_url_error), e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadFile(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _text.value = file.readText(Charset.defaultCharset())
                logger.success(getString(Res.string.generator_view_model_load_file_success))
            } catch (e: IOException) {
                logger.error(getString(Res.string.generator_view_model_load_file_error), e)
            }
            _fileName.value = file.absolutePath
        }
    }

    suspend fun generatePDF(): ByteArray? {
        require(_text.value.isNotBlank()) { "text cannot be empty" }
        require(_isGenerating.value.not()) { "pdf is already generating" }
        _isGenerating.value = true
        try {
            return converter.convert(_text.value).also {
                logger.success(getString(Res.string.generator_view_model_generate_pdf_success))
            }
        } catch (e: Exception) {
            // prevent crash if unknown error occurs
            logger.error(getString(Res.string.generator_view_model_generate_pdf_success), e)
            return null
        } finally {
            _isGenerating.value = false
        }
    }
}
