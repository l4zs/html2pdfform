package de.l4zs.html2pdfform.backend.converter

import com.lowagie.text.BadElementException
import com.lowagie.text.Document
import com.lowagie.text.DocumentException
import com.lowagie.text.Image
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.data.field.*
import de.l4zs.html2pdfform.backend.extension.*
import de.l4zs.html2pdfform.backend.config.ConfigContext
import de.l4zs.html2pdfform.util.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.ByteArrayOutputStream
import java.io.IOException
import com.lowagie.text.Document as PdfDocument
import org.jsoup.nodes.Document as HtmlDocument

class HtmlConverter(
    private val logger: Logger,
    private val configContext: ConfigContext,
) : Converter {
    private val config
        get() = configContext.config

    override fun convert(input: String): ByteArray? {
        val html = Jsoup.parse(input)

        if (html.forms().isEmpty()) {
            logger.warn("Kein Formular gefunden")
            return null
        }

        val pdf =
            PdfDocument(
                config.pageSize.toPdfRectangle(),
                config.pagePaddingX,
                config.pagePaddingX,
                config.pagePaddingY / 2,
                config.pagePaddingY / 2,
            )
        val locationHandler = LocationHandler(pdf, config)
        val outputStream = ByteArrayOutputStream()
        val writer =
            try {
                PdfWriter.getInstance(pdf, outputStream)
            } catch (e: DocumentException) {
                logger.error("Fehler beim Initialisieren des PdfWriter", e)
                return null
            }

        writer.setPdfVersion(PdfWriter.PDF_VERSION_1_7)
        pdf.setMetadata(config)
        pdf.setFirstPageHeaderFooter(config)

        pdf.open()

        pdf.setHeaderFooter(config)
        writeIntro(pdf, locationHandler, writer)
        convertForms(html, locationHandler, writer)

        pdf.close()

        return outputStream.toByteArray()
    }

    /**
     * Writes the intro text to the PDF. (optional Image and text at the
     * beginning of the PDF)
     */
    private fun writeIntro(
        pdf: Document,
        locationHandler: LocationHandler,
        writer: PdfWriter,
    ) {
        var spacingBefore = 0f
        if (config.intro.imageEnabled && config.intro.image != null) {
            val image =
                try {
                    Image.getInstance(config.intro.image!!.path)
                } catch (e: BadElementException) {
                    logger.error("Fehler beim Erstellen des Logos", e)
                    null
                } catch (e: IOException) {
                    logger.error("Fehler beim Laden der Logo-Datei", e)
                    null
                }
            if (image != null) {
                image.scaleToFit(
                    config.intro.image!!
                        .width
                        .coerceAtMost(config.effectivePageWidth),
                    config.effectivePageHeight,
                )
                image.setAbsolutePosition(
                    config.pageMinX,
                    config.pageMaxY - image.scaledHeight,
                )
                pdf.add(image)
                locationHandler.currentY -= image.scaledHeight + config.groupPaddingY
                spacingBefore += image.scaledHeight
            }
        }
        if (config.intro.textEnabled && config.intro.text != null) {
            writer.pageEvent =
                object : com.lowagie.text.pdf.PdfPageEventHelper() {
                    override fun onParagraphEnd(
                        writer: PdfWriter?,
                        document: Document?,
                        paragraphPosition: Float,
                    ) {
                        // this is kind of a hack to get the position of the last paragraph
                        locationHandler.currentY = paragraphPosition - config.groupPaddingY
                    }
                }
            val paragraph =
                Paragraph(config.intro.text!!.text).apply {
                    this.spacingBefore = spacingBefore + config.groupPaddingY
                    font = config.introFont
                    alignment = Paragraph.ALIGN_LEFT
                }
            pdf.add(paragraph)
        }
    }

    private fun convertForms(
        html: HtmlDocument,
        locationHandler: LocationHandler,
        writer: PdfWriter,
    ) {
        html.forms().forEach { htmlForm ->
            val context =
                Context(
                    writer.acroForm,
                    writer,
                    logger,
                    config,
                )

            htmlForm.convert(context)?.forEach {
                it.rectangle = locationHandler.adjustRectangle(it.rectangle)
                it.write()
            }
        }
    }
}

fun Element.convert(context: Context): List<FormField>? {
    if (id().isNotBlank() && context.convertedIds.contains(id())) {
        return null
    }
    if (id().isNotBlank()) {
        context.convertedIds.add(id())
    }

    return when (tagName()) {
        "input" -> {
            val field = convertInput(context)
            if (field != null) {
                listOf(field)
            } else {
                null
            }
        }

        "fieldset" -> {
            listOf(fieldset(this, context))
        }

        "textarea" -> {
            listOf(textarea(this, context))
        }

        "signature" -> {
            listOf(signature(this, context))
        }

        "select" -> {
            listOf(select(this, context))
        }

        else -> children().mapNotNull { it.convert(context) }.flatten()
    }
}

fun Element.convertInput(context: Context): FormField? {
    return when (this.attr("type")) {
        "checkbox" -> {
            checkbox(this, context)
        }

        "date" -> {
            date(this, context)
        }

        "datetime-local" -> {
            datetimeLocal(this, context)
        }

        "email" -> {
            email(this, context)
        }

        "file" -> {
            file(this, context)
        }

        "hidden" -> {
            hidden(this, context)
        }

        "month" -> {
            month(this, context)
        }

        "number" -> {
            number(this, context)
        }

        "password" -> {
            password(this, context)
        }

        "radio" -> {
            if (context.radioGroups.containsKey(this.attr("name"))) {
                null
            } else {
                radioGroup(this, context)
            }
        }

        "reset" -> {
            reset(this, context)
        }

        "submit" -> {
            submit(this, context)
        }

        "tel" -> {
            telephone(this, context)
        }

        "text" -> {
            text(this, context)
        }

        "time" -> {
            time(this, context)
        }

        "url" -> {
            url(this, context)
        }

        else -> {
            context.logger.info(
                "Input mit dem Typ ${attr("type")} ${
                    if (id().isNotBlank()) "(id: ${id()})" else ""
                } ist nicht unterstützt",
            )
            return null
        }
    }
}

actual fun createConverter(
    logger: Logger,
    configContext: ConfigContext,
): Converter = HtmlConverter(logger, configContext)
