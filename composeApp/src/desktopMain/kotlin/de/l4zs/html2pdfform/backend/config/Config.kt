package de.l4zs.html2pdfform.backend.config

import de.l4zs.html2pdfform.util.Logger
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

actual fun saveConfigToFile(
    config: Config,
    logger: Logger,
) {
    if (!configFile.exists()) {
        configFile.parentFile.mkdirs()
        configFile.createNewFile()
    }
    if (writeConfig(config, logger)) {
        logger.success("Einstellungen erfolgreich gespeichert")
    }
}

actual fun loadConfigFromFile(logger: Logger): Config {
    configFile = File(filepath)
    if (!configFile.exists()) {
        configFile.parentFile.mkdirs()
        configFile.createNewFile()
        return Config().also { writeConfig(it, logger) }
    } else {
        try {
            return Json.decodeFromString<Config>(configFile.readText())
        } catch (e: IOException) {
            logger.warn("Fehler beim Laden der Config-Datei", e)
        } catch (e: SecurityException) {
            logger.warn("Fehlende Rechte beim Zugriff auf die Config-Datei", e)
        } catch (e: SerializationException) {
            logger.warn("Fehler beim Deserialisieren der Config", e)
        } catch (e: IllegalArgumentException) {
            logger.warn("Config-Datei enthält keine gültige Config", e)
        }
        logger.warn("Config-Datei konnte nicht geladen werden. Standardwerte werden stattdessen verwendet")
        return Config()
    }
}

private lateinit var configFile: File

private val filepath =
    when {
        System.getProperty("os.name").contains("win", true) -> {
            System.getenv("APPDATA") + "/html2pdfform/config.json"
        }

        System.getProperty("os.name").contains("mac", true) -> {
            System.getProperty("user.home") + "/Library/Preferences/html2pdfform/config.json"
        }

        else -> System.getProperty("user.home") + "/.config/html2pdfform/config.json"
    }

private fun writeConfig(
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
