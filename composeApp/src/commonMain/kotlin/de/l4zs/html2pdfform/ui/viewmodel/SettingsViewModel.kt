package de.l4zs.html2pdfform.ui.viewmodel

import androidx.lifecycle.ViewModel
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.config.ConfigContext
import de.l4zs.html2pdfform.backend.config.saveConfigToFile
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val logger: Logger,
    private val cfg: ConfigContext,
) : ViewModel() {
    private val _config = MutableStateFlow(cfg.config)
    val config = _config.asStateFlow()

    fun updateConfig(newConfig: Config) {
        _config.value = newConfig
    }

    fun saveConfig() {
        cfg.config = _config.value
        saveConfigToFile(cfg.config, logger)
        logger.success("Einstellungen gespeichert")
    }
}
