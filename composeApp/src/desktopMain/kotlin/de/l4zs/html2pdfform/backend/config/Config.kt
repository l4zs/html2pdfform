package de.l4zs.html2pdfform.backend.config

import de.l4zs.html2pdfform.resources.*
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString
import java.io.File
import java.io.IOException

actual suspend fun saveConfigToFile(
    config: Config,
    logger: Logger,
) {
    if (!configFile.exists()) {
        configFile.parentFile.mkdirs()
        withContext(Dispatchers.IO) {
            try {
                configFile.createNewFile()
            } catch (e: IOException) {
                logger.error(getString(Res.string.config_file_create_error), e)
            }
        }
    }
    if (writeConfig(config, logger)) {
        logger.success(getString(Res.string.config_saved))
    }
}

actual suspend fun loadConfigFromFile(logger: Logger): Config {
    configFile = File(filepath())
    if (!configFile.exists()) {
        configFile.parentFile.mkdirs()
        withContext(Dispatchers.IO) {
            try {
                configFile.createNewFile()
            } catch (e: IOException) {
                logger.error(getString(Res.string.config_file_create_error), e)
            }
        }
        return Config().also { writeConfig(it, logger) }
    } else {
        try {
            return Json.decodeFromString<Config>(configFile.readText())
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

actual suspend fun loadConfigFromFile(
    logger: Logger,
    path: String,
): Config? {
    configFile = File(path)
    if (!configFile.exists()) {
        logger.warn(getString(Res.string.config_not_exist))
        return null
    } else {
        try {
            return Json.decodeFromString<Config>(configFile.readText())
        } catch (e: IOException) {
            logger.warn(getString(Res.string.config_load_error_io), e)
        } catch (e: SecurityException) {
            logger.warn(getString(Res.string.config_load_error_security), e)
        } catch (e: SerializationException) {
            logger.warn(getString(Res.string.config_load_error_serialization), e)
        } catch (e: IllegalArgumentException) {
            logger.warn(getString(Res.string.config_load_error_argument), e)
        }
        logger.warn(getString(Res.string.config_load_error_nodefault))
        return null
    }
}

actual fun configFile() = configFile

private lateinit var configFile: File

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
): Boolean {
    try {
        configFile.writeText(Json.encodeToString(config))
        return true
    } catch (e: SerializationException) {
        logger.warn("Fehler beim Serialisieren der Config", e)
    } catch (e: IllegalArgumentException) {
        logger.warn("Fehler beim Format der Config", e)
    } catch (e: IOException) {
        logger.warn("Fehler beim Schreiben der Config-Datei", e)
    } catch (e: SecurityException) {
        logger.warn("Fehlende Rechte beim Zugriff auf die Config-Datei", e)
    }
    return false
}
