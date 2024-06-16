package de.janniskramer.htmlform2pdfform.data

import com.lowagie.text.pdf.PdfAcroForm
import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.PdfWriter
import de.janniskramer.htmlform2pdfform.converter.HtmlConverter
import de.janniskramer.htmlform2pdfform.converter.LocationHandler

data class Context(
    val locationHandler: LocationHandler,
    val converter: HtmlConverter,
    val acroForm: PdfAcroForm,
    val writer: PdfWriter,
) {
    var currentElementIndex = 0
        get() = field++ // auto-increment

    val radioGroups = mutableMapOf<String, PdfFormField>()
    val convertedIds = mutableSetOf<String>()
}
