package de.l4zs.html2pdfform.backend.extension

import com.lowagie.text.Phrase
import de.l4zs.html2pdfform.backend.config.HeaderFooter
import com.lowagie.text.HeaderFooter as PdfHeaderFooter

fun HeaderFooter.asPdfHeaderFooter(): PdfHeaderFooter =
    (
        if (before.isNotEmpty() && after.isNotEmpty()) {
            PdfHeaderFooter(Phrase("$before "), Phrase(after))
        } else if (before.isNotEmpty()) {
            PdfHeaderFooter(Phrase("$before "), overrideNumbered)
        } else if (after.isNotEmpty()) {
            PdfHeaderFooter(overrideNumbered, Phrase(" $after"))
        } else {
            PdfHeaderFooter(Phrase(" "), overrideNumbered)
        }
    ).apply {
        setAlignment(align.align)
        borderWidth = 0f
    }
