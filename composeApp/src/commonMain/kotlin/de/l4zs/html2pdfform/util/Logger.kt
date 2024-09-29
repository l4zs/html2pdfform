package de.l4zs.html2pdfform.util

import de.l4zs.html2pdfform.backend.data.Translatable
import de.l4zs.html2pdfform.resources.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.compose.resources.StringResource

/**
 * A simple logger that can be used to log messages with different log
 * levels. The log messages are stored in a list and can be accessed via
 * the [history] property.
 *
 * @param logLevel The log level that should be used. Only messages with a
 *    log level equal or higher than this will be stored.
 */
class Logger(
    var logLevel: LogLevel = LogLevel.INFO,
) {
    private val _history = MutableStateFlow<List<LogEntry>>(listOf())
    val history = _history.asStateFlow()

    private fun add(logEntry: LogEntry) {
        if (logEntry.level > logLevel) return
        _history.value += logEntry
    }

    fun remove(logEntry: LogEntry) {
        _history.value -= logEntry
    }

    fun clear() {
        _history.value = listOf()
    }

    /**
     * Logs a [LogLevel.SUCCESS] message.
     *
     * @param message The message to log.
     */
    fun success(message: String) {
        add(LogEntry(LogLevel.SUCCESS, message, null))
    }

    /**
     * Logs an [LogLevel.ERROR] message.
     *
     * @param message The message to log.
     * @param error The error that caused the message.
     */
    fun error(
        message: String,
        error: Throwable? = null,
    ) {
        add(LogEntry(LogLevel.ERROR, message, error))
    }

    /**
     * Logs a [LogLevel.WARN] message.
     *
     * @param message The message to log.
     * @param error The error that caused the message.
     */
    fun warn(
        message: String,
        error: Throwable? = null,
    ) {
        add(LogEntry(LogLevel.WARN, message, error))
    }

    /**
     * Logs a [LogLevel.INFO] message.
     *
     * @param message The message to log.
     * @param error The error that caused the message.
     */
    fun info(
        message: String,
        error: Throwable? = null,
    ) {
        add(LogEntry(LogLevel.INFO, message, error))
    }

    /**
     * Logs a [LogLevel.DEBUG] message.
     *
     * @param message The message to log.
     * @param error The error that caused the message.
     */
    fun debug(
        message: String,
        error: Throwable? = null,
    ) {
        add(LogEntry(LogLevel.DEBUG, message, error))
    }

    /**
     * The different log levels that can be used.
     *
     * @constructor Create empty Log level
     * @property translationKey The translation key for the log level.
     * @property resource The resource for the log level.
     */
    enum class LogLevel(
        @Transient
        override val translationKey: String,
        @Transient
        override val resource: StringResource,
    ) : Translatable {
        SUCCESS("loglevel_success", Res.string.loglevel_success),
        NONE("loglevel_none", Res.string.loglevel_none),
        ERROR("loglevel_error", Res.string.loglevel_error),
        WARN("loglevel_warn", Res.string.loglevel_warn),
        INFO("loglevel_info", Res.string.loglevel_info),
        DEBUG("loglevel_debug", Res.string.loglevel_debug),
    }

    /**
     * A log entry that contains the log level, the message, the error that
     * caused the message and the time when the message was logged.
     *
     * @property level The log level of the message.
     * @property message The message that was logged.
     * @property error The error that caused the message.
     * @property time The time when the message was logged.
     */
    data class LogEntry(
        val level: LogLevel,
        val message: String,
        val error: Throwable?,
        val time: Long = System.currentTimeMillis(),
    )
}
