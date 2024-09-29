package de.l4zs.html2pdfform.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.converter.Converter
import de.l4zs.html2pdfform.resources.*
import de.l4zs.html2pdfform.util.Logger
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.charset.Charset

/**
 * ViewModel for the GeneratorView.
 *
 * @param logger Logger to log messages.
 * @param converter Converter to convert HTML to PDF.
 * @param config Config to configure the HttpClient.
 */
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

    private val client =
        HttpClient(OkHttp) {
            expectSuccess = true
            engine {
                config {
                    followRedirects(true)
                    followSslRedirects(true)
                    retryOnConnectionFailure(true)
                }
            }
        }

    fun updateUrl(newUrl: String) {
        _url.value = newUrl
    }

    fun updateFileName(newFileName: String) {
        _fileName.value = newFileName
    }

    fun updateText(newText: String) {
        _text.value = newText
    }

    /** Load the content of the given URL to the text field. */
    fun loadUrl() {
        require(url.value.isNotBlank()) { "url cannot be empty" }
        require(isLoading.value.not()) { "url is already loading" }
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = client.get(url.value)
                _text.value = response.bodyAsText()
                logger.success(getString(Res.string.generator_view_model_load_url_success))
            } catch (e: SocketTimeoutException) {
                logger.error(getString(Res.string.generator_view_model_load_url_error_timeout), e)
            } catch (e: UnknownHostException) {
                logger.error(getString(Res.string.generator_view_model_load_url_error_host), e)
            } catch (e: ClientRequestException) {
                logger.error(getString(Res.string.generator_view_model_load_url_error_4xx), e)
            } catch (e: ServerResponseException) {
                logger.error(getString(Res.string.generator_view_model_load_url_error_5xx), e)
            } catch (e: IllegalArgumentException) {
                logger.error(getString(Res.string.generator_view_model_load_url_error_url), e)
            } catch (e: ConnectException) {
                logger.error(getString(Res.string.generator_view_model_load_url_error_connect), e)
            } catch (e: IOException) {
                logger.error(getString(Res.string.generator_view_model_load_url_error_io), e)
            } catch (e: Exception) {
                // Catch any other exceptions
                logger.error(getString(Res.string.generator_view_model_load_url_error), e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load the content of the given file to the text field.
     *
     * @param file File to load.
     */
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

    /**
     * Generate a PDF from the text content.
     *
     * @return PDF as ByteArray or null if an error occurred.
     */
    suspend fun generatePDF(): ByteArray? {
        require(text.value.isNotBlank()) { "text cannot be empty" }
        require(isGenerating.value.not()) { "pdf is already generating" }
        _isGenerating.value = true
        try {
            return converter.convert(text.value).also {
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
