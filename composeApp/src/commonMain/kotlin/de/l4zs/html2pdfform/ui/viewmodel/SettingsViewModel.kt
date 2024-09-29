package de.l4zs.html2pdfform.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.l4zs.html2pdfform.backend.config.*
import de.l4zs.html2pdfform.backend.data.toLocale
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.settings_view_model_load_config_error
import de.l4zs.html2pdfform.resources.settings_view_model_load_config_success
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import java.io.File
import java.util.*

/**
 * ViewModel for the settings view.
 *
 * @param logger The logger to use.
 * @param configContext The config context to use.
 */
class SettingsViewModel(
    private val logger: Logger,
    private val configContext: ConfigContext,
) : ViewModel() {
    private val _config = MutableStateFlow(configContext.config)
    val config = _config.asStateFlow()

    val isConfigChanged: Boolean
        get() = _config.value != configContext.config

    fun updateConfig(newConfig: Config) {
        _config.value = newConfig
    }

    /**
     * Loads a config from a file.
     * The changes are not applied until [saveConfig] is called.
     *
     * @param file The file to load the config from.
     */
    fun loadConfig(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _config.value = loadConfigFromFile(logger, file)
                logger.success(getString(Res.string.settings_view_model_load_config_success))
            } catch (e: Exception) {
                logger.warn(getString(Res.string.settings_view_model_load_config_error), e)
            }
        }
    }

    /**
     * Applies the changes to the config.
     */
    suspend fun saveConfig() {
        configContext.config = _config.value
        Locale.setDefault(configContext.config.language.toLocale())
        logger.logLevel = configContext.config.logLevel
        saveConfigToFile(configContext.config, logger)
    }

    /**
     * Exports the current config as a byte array.
     *
     * @return The exported config as a byte array.
     */
    fun exportConfig(): ByteArray = exportConfig(config.value).toByteArray(Charsets.UTF_8)
}
