package de.l4zs.html2pdfform.backend.config

/**
 * This class is used to store the config object in a context to make it
 * accessible in the whole application and to allow modifications.
 *
 * @constructor Create empty Config context
 * @property config The config object
 */
data class ConfigContext(
    var config: Config,
)
