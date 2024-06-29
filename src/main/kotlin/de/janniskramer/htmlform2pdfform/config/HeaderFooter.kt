package de.janniskramer.htmlform2pdfform.config

import com.lowagie.text.Phrase
import com.lowagie.text.HeaderFooter as PdfHeaderFooter

// when before and after set, numbered is always true
data class HeaderFooter(
    val before: String? = null,
    val after: String? = null,
    val numbered: Boolean = true,
    val align: Int = PdfHeaderFooter.ALIGN_RIGHT,
) {
    fun asPdfHeaderFooter(): PdfHeaderFooter =
        (
            before?.let { b ->
                after?.let { PdfHeaderFooter(Phrase("$b "), Phrase(" $it")) }
                    ?: PdfHeaderFooter(Phrase("$b "), numbered)
            }
                ?: after?.let { PdfHeaderFooter(numbered, Phrase(" $it")) }
                ?: PdfHeaderFooter(numbered)
        ).apply {
            setAlignment(align)
            borderWidth = 0f
        }
}
