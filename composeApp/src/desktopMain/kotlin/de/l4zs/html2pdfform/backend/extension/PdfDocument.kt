package de.l4zs.html2pdfform.backend.extension

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfDate
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.app_name
import de.l4zs.html2pdfform.resources.app_version
import org.jetbrains.compose.resources.getString
import java.util.*

/**
 * Sets the metadata of the PDF document
 *
 * @param config The configuration of the PDF document
 */
suspend fun Document.setMetadata(config: Config) {
    addAuthor(config.metadata.author)
    addCreator(config.metadata.creator)
    addSubject(config.metadata.subject)
    addCreationDate(PdfDate(Calendar.getInstance(TimeZone.getDefault())))
    addProducer("${getString(Res.string.app_name)} v${getString(Res.string.app_version)}")
}

/**
 * Sets the first page header and footer of the PDF document
 *
 * @param config The configuration of the PDF document
 */
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

/**
 * Sets the header and footer of the PDF document
 *
 * @param config The configuration of the PDF document
 */
fun Document.setHeaderFooter(config: Config) {
    val header = config.header.asPdfHeaderFooter()
    setHeader(header)
    val footer = config.footer.asPdfHeaderFooter()
    setFooter(footer)
}
