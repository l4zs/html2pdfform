package de.l4zs.html2pdfform.util

interface Logger {
    var logLevel: LogLevel

    fun error(
        message: String,
        error: Throwable?,
    )

    fun warn(
        message: String,
        error: Throwable?,
    )

    fun success(
        message: String,
        error: Throwable?,
    )

    fun info(
        message: String,
        error: Throwable?,
    )

    fun debug(
        message: String,
        error: Throwable?,
    )

    fun error(message: String) = error(message, null)

    fun warn(message: String) = warn(message, null)

    fun success(message: String) = success(message, null)

    fun info(message: String) = info(message, null)

    fun debug(message: String) = debug(message, null)

    enum class LogLevel {
        ERROR,
        WARN,
        SUCCESS,
        INFO,
        DEBUG,
    }
}
