package de.l4zs.html2pdfform.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Logger(
    var logLevel: LogLevel = LogLevel.INFO,
) {
    private val _history = MutableStateFlow<List<LogEntry>>(listOf())
    val history = _history.asStateFlow()

    private fun add(logEntry: LogEntry) {
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

    enum class LogLevel {
        ERROR,
        WARN,
        SUCCESS,
        INFO,
        DEBUG,
    }

    data class LogEntry(
        val level: LogLevel,
        val message: String,
        val error: Throwable?,
        val time: Long = System.currentTimeMillis(),
    )
}
