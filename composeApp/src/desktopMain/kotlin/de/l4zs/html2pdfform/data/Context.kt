package de.l4zs.html2pdfform.data

import com.lowagie.text.pdf.PdfAcroForm
import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.PdfWriter
import de.l4zs.html2pdfform.util.Logger

data class Context(
    val acroForm: PdfAcroForm,
    val writer: PdfWriter,
    val logger: Logger,
) {
    var currentElementIndex = 0
        get() = field++ // auto-increment

    val radioGroups = mutableMapOf<String, PdfFormField>()
    val convertedIds = mutableSetOf<String>()
}
