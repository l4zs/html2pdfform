package de.janniskramer.htmlform2pdfform.extensions

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfDate
import de.janniskramer.htmlform2pdfform.config
import java.util.*

fun Document.setMetadata() {
    addAuthor(config.metadata.author)
    addCreator(config.metadata.creator)
    addSubject(config.metadata.subject)
    addCreationDate(PdfDate(Calendar.getInstance(TimeZone.getDefault())))
    addProducer("htmlform2pdfform")
}

fun Document.setFirstPageHeaderFooter() {
    val header = config.firstPageHeader.asPdfHeaderFooter()
    setHeader(header)
    val footer = config.firstPageFooter.asPdfHeaderFooter()
    setFooter(footer)
}

fun Document.setHeaderFooter() {
    val header = config.header.asPdfHeaderFooter()
    setHeader(header)
    val footer = config.footer.asPdfHeaderFooter()
    setFooter(footer)
}
