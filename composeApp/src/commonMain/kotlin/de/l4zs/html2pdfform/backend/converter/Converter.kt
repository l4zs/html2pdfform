package de.l4zs.html2pdfform.backend.converter

import de.l4zs.html2pdfform.backend.config.ConfigContext
import de.l4zs.html2pdfform.util.Logger

/** Interface for converting HTML to PDF */
interface Converter {
    /**
     * Converts the given HTML input to a PDF.
     *
     * @param input The HTML input to convert.
     * @return The PDF as a byte array or null if the conversion failed.
     */
    suspend fun convert(input: String): ByteArray?
}

/**
 * Factory function for creating a converter. This is platform specific and
 * should be implemented for each platform.
 *
 * @param logger The logger to use for logging.
 * @param configContext The configuration context to use for the converter.
 * @return The created [Converter].
 */
expect fun createConverter(
    logger: Logger,
    configContext: ConfigContext,
): Converter
