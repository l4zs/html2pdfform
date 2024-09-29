package de.l4zs.html2pdfform.backend.data

import com.lowagie.text.pdf.PdfAcroForm
import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.PdfWriter
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.util.Logger

/**
 * Context object that holds all necessary data for the conversion process.
 *
 * @param acroForm The PDF AcroForm object.
 * @param writer The PDF writer object.
 * @param logger The logger object.
 * @param config The configuration object.
 */
data class Context(
    val acroForm: PdfAcroForm,
    val writer: PdfWriter,
    val logger: Logger,
    val config: Config,
) {
    /**
     * The current element index. This is used to generate unique element IDs. It is auto-incremented.
     */
    var currentElementIndex = 0
        get() = field++ // auto-increment

    val radioGroups = mutableMapOf<String, PdfFormField>()
    val convertedIds = mutableSetOf<String>()
}
