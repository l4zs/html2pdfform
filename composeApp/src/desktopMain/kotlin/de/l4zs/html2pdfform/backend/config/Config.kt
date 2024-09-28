@file:JvmName("ConfigKtJvm") // JvmName is needed to avoid name conflicts with the Config class in the common module

package de.l4zs.html2pdfform.backend.config

import de.l4zs.html2pdfform.resources.*
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString
import java.io.File
import java.io.IOException

actual suspend fun saveConfigToFile(
    config: Config,
    logger: Logger,
    file: File,
) {
    if (!file.exists()) {
        file.parentFile.mkdirs()
        withContext(Dispatchers.IO) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                logger.error(getString(Res.string.config_file_create_error), e)
            }
        }
    }
    if (writeConfig(config, logger, file)) {
        logger.success(getString(Res.string.config_saved))
    }
}

actual suspend fun loadConfigFromFile(
    logger: Logger,
    file: File,
): Config {
    if (!file.exists()) {
        file.parentFile.mkdirs()
        withContext(Dispatchers.IO) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                logger.error(getString(Res.string.config_file_create_error), e)
            }
        }
        return Config().also { writeConfig(it, logger) }
    } else {
        try {
            return importConfig(file.readText())
        } catch (e: IOException) {
            logger.warn(getString(Res.string.config_load_error_io), e)
        } catch (e: SecurityException) {
            logger.warn(getString(Res.string.config_load_error_security), e)
        } catch (e: SerializationException) {
            logger.warn(getString(Res.string.config_load_error_serialization), e)
        } catch (e: IllegalArgumentException) {
            logger.warn(getString(Res.string.config_load_error_argument), e)
        }
        logger.warn(getString(Res.string.config_load_error_default))
        return Config()
    }
}

actual fun configFile() = configFile

private var configFile = File(filepath())

private fun filepath(path: String = "html2pdfform") =
    when {
        System.getProperty("os.name").contains("win", true) -> {
            System.getenv("APPDATA") + "/$path/config.json"
        }

        System.getProperty("os.name").contains("mac", true) -> {
            System.getProperty("user.home") + "/Library/Preferences/$path/config.json"
        }

        else -> System.getProperty("user.home") + "/.config/$path/config.json"
    }

private suspend fun writeConfig(
    config: Config,
    logger: Logger,
    file: File = configFile,
): Boolean {
    try {
        file.writeText(exportConfig(config))
        return true
    } catch (e: SerializationException) {
        logger.warn(getString(Res.string.config_save_error_serialization), e)
    } catch (e: IllegalArgumentException) {
        logger.warn(getString(Res.string.config_save_error_argument), e)
    } catch (e: IOException) {
        logger.warn(getString(Res.string.config_save_error_io), e)
    } catch (e: SecurityException) {
        logger.warn(getString(Res.string.config_save_error_security), e)
    }
    return false
}
