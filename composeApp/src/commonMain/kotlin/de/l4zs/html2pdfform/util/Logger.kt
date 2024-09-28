package de.l4zs.html2pdfform.util

import de.l4zs.html2pdfform.backend.data.Translatable
import de.l4zs.html2pdfform.resources.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.compose.resources.StringResource

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

    fun error(
        message: String,
        error: Throwable? = null,
    ) {
        add(LogEntry(LogLevel.ERROR, message, error))
    }

    fun warn(
        message: String,
        error: Throwable? = null,
    ) {
        add(LogEntry(LogLevel.WARN, message, error))
    }

    fun success(
        message: String,
        error: Throwable? = null,
    ) {
        add(LogEntry(LogLevel.SUCCESS, message, error))
    }

    fun info(
        message: String,
        error: Throwable? = null,
    ) {
        add(LogEntry(LogLevel.INFO, message, error))
    }

    fun debug(
        message: String,
        error: Throwable? = null,
    ) {
        add(LogEntry(LogLevel.DEBUG, message, error))
    }

    enum class LogLevel(
        override val translationKey: String,
        override val resource: StringResource,
    ) : Translatable {
        SUCCESS("loglevel_success", Res.string.loglevel_success),
        ERROR("loglevel_error", Res.string.loglevel_error),
        WARN("loglevel_warn", Res.string.loglevel_warn),
        INFO("loglevel_info", Res.string.loglevel_info),
        DEBUG("loglevel_debug", Res.string.loglevel_debug),
    }

    data class LogEntry(
        val level: LogLevel,
        val message: String,
        val error: Throwable?,
        val time: Long = System.currentTimeMillis(),
    )
}
