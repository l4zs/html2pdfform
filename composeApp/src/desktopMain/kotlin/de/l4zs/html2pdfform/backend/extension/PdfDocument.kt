package de.l4zs.html2pdfform.backend.extension

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfDate
import de.l4zs.html2pdfform.backend.config.Config
import java.util.Calendar
import java.util.TimeZone

fun Document.setMetadata(config: Config) {
    addAuthor(config.metadata.author)
    addCreator(config.metadata.creator)
    addSubject(config.metadata.subject)
    addCreationDate(PdfDate(Calendar.getInstance(TimeZone.getDefault())))
    addProducer("html2pdfform")
}

fun Document.setFirstPageHeaderFooter(config: Config) {
    setHeaderFooter(config)
    if (config.firstPageHeaderEnabled) {
        val header = config.firstPageHeader?.asPdfHeaderFooter() ?: config.header.asPdfHeaderFooter()
        setHeader(header)
    }
    if (config.firstPageFooterEnabled) {
        val footer = config.firstPageFooter?.asPdfHeaderFooter() ?: config.footer.asPdfHeaderFooter()
        setFooter(footer)
    }
}

fun Document.setHeaderFooter(config: Config) {
    val header = config.header.asPdfHeaderFooter()
    setHeader(header)
    val footer = config.footer.asPdfHeaderFooter()
    setFooter(footer)
}
