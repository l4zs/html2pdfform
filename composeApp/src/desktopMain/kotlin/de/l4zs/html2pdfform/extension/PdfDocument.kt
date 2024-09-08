package de.l4zs.html2pdfform.extension

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfDate
import de.l4zs.html2pdfform.ui.config
import java.util.*

fun Document.setMetadata() {
    addAuthor(config.metadata.author)
    addCreator(config.metadata.creator)
    addSubject(config.metadata.subject)
    addCreationDate(PdfDate(Calendar.getInstance(TimeZone.getDefault())))
    addProducer("html2pdfform")
}

fun Document.setFirstPageHeaderFooter() {
    val header = config.firstPageHeader?.asPdfHeaderFooter() ?: config.header.asPdfHeaderFooter()
    setHeader(header)
    val footer = config.firstPageFooter?.asPdfHeaderFooter() ?: config.footer.asPdfHeaderFooter()
    setFooter(footer)
}

fun Document.setHeaderFooter() {
    val header = config.header.asPdfHeaderFooter()
    setHeader(header)
    val footer = config.footer.asPdfHeaderFooter()
    setFooter(footer)
}
