package de.l4zs.html2pdfform.backend.converter

import de.l4zs.html2pdfform.ui.util.ConfigContext
import de.l4zs.html2pdfform.util.Logger

interface Converter {
    fun convert(input: String): ByteArray?
}

expect fun createConverter(
    logger: Logger,
    configContext: ConfigContext,
): Converter
