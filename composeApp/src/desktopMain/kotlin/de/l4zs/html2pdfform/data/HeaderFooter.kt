package de.l4zs.html2pdfform.data

import com.lowagie.text.Phrase
import com.lowagie.text.HeaderFooter as PdfHeaderFooter

// when before and after not empty, numbered is always true
data class HeaderFooter(
    val before: String = "",
    val after: String = "",
    val numbered: Boolean = true,
    val align: Int = PdfHeaderFooter.ALIGN_RIGHT,
) {
    fun asPdfHeaderFooter(): PdfHeaderFooter =
        (
            if (before.isNotEmpty() && after.isNotEmpty()) {
                PdfHeaderFooter(Phrase("$before "), Phrase(after))
            } else if (before.isNotEmpty()) {
                PdfHeaderFooter(Phrase("$before "), numbered)
            } else if (after.isNotEmpty()) {
                PdfHeaderFooter(numbered, Phrase(" $after"))
            } else {
                PdfHeaderFooter(Phrase(" "), numbered)
            }
        ).apply {
            setAlignment(align)
            borderWidth = 0f
        }
}
