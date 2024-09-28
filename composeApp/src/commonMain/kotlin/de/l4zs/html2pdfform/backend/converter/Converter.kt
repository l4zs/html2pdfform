package de.l4zs.html2pdfform.backend.converter

import de.l4zs.html2pdfform.backend.config.ConfigContext
import de.l4zs.html2pdfform.util.Logger

interface Converter {
    suspend fun convert(input: String): ByteArray?
}

expect fun createConverter(
    logger: Logger,
    configContext: ConfigContext,
): Converter
