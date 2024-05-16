package de.jannis_kramer.htmlform2pdfform.convert

import com.lowagie.text.pdf.PdfFormField
import com.lowagie.text.pdf.PdfName

fun Int.pad(length: Int): String {
    return this.toString().padStart(length, '0')
}

fun String.indentLines(length: Int = 4): String {
    return this.lines().joinToString("\n" + " ".repeat(length))
}

fun String.capitalizeFirst(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

val PdfFormField.mappingName: String
    get() = this.getAsString(PdfName.TM).toString()
