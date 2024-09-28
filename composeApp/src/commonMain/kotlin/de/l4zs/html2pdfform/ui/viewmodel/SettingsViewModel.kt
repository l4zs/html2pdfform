package de.l4zs.html2pdfform.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.l4zs.html2pdfform.backend.config.*
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.generator_view_model_load_url_error
import de.l4zs.html2pdfform.resources.settings_view_model_load_config_error
import de.l4zs.html2pdfform.resources.settings_view_model_load_config_success
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString
import java.io.File
import java.util.*

class SettingsViewModel(
    private val logger: Logger,
    private val cfg: ConfigContext,
) : ViewModel() {
    private val _config = MutableStateFlow(cfg.config)
    val config = _config.asStateFlow()

    val isConfigChanged: Boolean
        get() = _config.value != cfg.config

    fun updateConfig(newConfig: Config) {
        _config.value = newConfig
    }

    fun loadConfig(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _config.value = loadConfigFromFile(logger, file.path) ?: return@launch
                logger.success(getString(Res.string.settings_view_model_load_config_success))
            } catch (e: Exception) {
                logger.warn(getString(Res.string.settings_view_model_load_config_error), e)
            }
        }
    }

    suspend fun saveConfig() {
        cfg.config = _config.value
        Locale.setDefault(cfg.config.language.toLocale())
        saveConfigToFile(cfg.config, logger)
    }

    fun exportConfig(): ByteArray = Json.encodeToString(config.value).toByteArray(Charsets.UTF_8)
}
