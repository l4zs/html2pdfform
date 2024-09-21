package de.l4zs.html2pdfform.backend.extension

import com.lowagie.text.Font
import com.lowagie.text.pdf.BaseFont
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.util.Logger
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException


val Config.baseFont: BaseFont
    get() = BaseFont.createFont(font.displayName, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
val Config.introBaseFont: BaseFont
    get() =
        BaseFont.createFont(
            intro.text?.font?.displayName ?: font.displayName,
            BaseFont.CP1252,
            BaseFont.NOT_EMBEDDED,
        )
val Config.defaultFont
    get() = Font(baseFont, fontSize)
val Config.introFont
    get() = Font(introBaseFont, intro.text?.fontSize ?: fontSize)

private lateinit var config: Config

lateinit var configFile: File
    private set

private val filepath = System.getenv("APPDATA") + "\\html2pdfform\\config.json"

fun Config.Companion.loadConfig(logger: Logger) {
    configFile = File(filepath)
    if (!configFile.exists()) {
        configFile.parentFile.mkdirs()
        configFile.createNewFile()
        config = Config()
        writeConfig(logger)
    } else {
        var readCorrectly = false
        try {
            config = Json.decodeFromString<Config>(configFile.readText())
            readCorrectly = true
        } catch (e: IOException) {
            logger.warn("Fehler beim Laden der Config-Datei", e)
        } catch (e: SecurityException) {
            logger.warn("Fehlende Rechte beim Zugriff auf die Config-Datei", e)
        } catch (e: SerializationException) {
            logger.warn("Fehler beim Deserialisieren der Config", e)
        } catch (e: IllegalArgumentException) {
            logger.warn("Config-Datei enthält keine gültige Config", e)
        }
        if (!readCorrectly) {
            logger.warn("Config-Datei konnte nicht geladen werden. Standardwerte werden stattdessen verwendet")
            config = Config()
        }
    }
}

fun Config.Companion.saveConfig(logger: Logger) {
    if (!configFile.exists()) {
        configFile.parentFile.mkdirs()
        configFile.createNewFile()
    }
    if (writeConfig(logger)) {
        logger.success("Einstellungen erfolgreich gespeichert")
    }
}

private fun writeConfig(logger: Logger): Boolean {
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
