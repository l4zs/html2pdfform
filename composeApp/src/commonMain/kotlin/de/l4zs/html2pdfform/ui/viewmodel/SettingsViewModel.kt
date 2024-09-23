package de.l4zs.html2pdfform.ui.viewmodel

import androidx.lifecycle.ViewModel
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.config.ConfigContext
import de.l4zs.html2pdfform.backend.config.saveConfigToFile
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

    fun saveConfig() {
        cfg.config = _config.value
        saveConfigToFile(cfg.config, logger)
    }

    fun exportConfig(): ByteArray = Json.encodeToString(config.value).toByteArray(Charsets.UTF_8)
}
