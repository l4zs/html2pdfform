package de.l4zs.html2pdfform.backend.converter

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfWriter
import de.l4zs.html2pdfform.backend.config.ConfigContext
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.data.field.*
import de.l4zs.html2pdfform.backend.extension.*
import de.l4zs.html2pdfform.resources.*
import de.l4zs.html2pdfform.util.Logger
import org.jetbrains.compose.resources.getString
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.ByteArrayOutputStream
import java.io.IOException
import com.lowagie.text.Document as PdfDocument
import org.jsoup.nodes.Document as HtmlDocument

/**
 * The HtmlConverter converts HTML forms to PDF forms. It uses Jsoup to
 * parse the HTML and OpenPDF to create the PDF. This is the platform
 * specific implementation for JVM-Desktop.
 *
 * @property logger The logger
 * @property configContext The config context
 */
class HtmlConverter(
    private val logger: Logger,
    private val configContext: ConfigContext,
) : Converter {
    private val config
        get() = configContext.config

    /**
     * Converts the given HTML string to a PDF byte array.
     *
     * @param input The HTML string
     * @return The PDF byte array or null if an error occurred
     */
    override suspend fun convert(input: String): ByteArray? {
        val html = Jsoup.parse(input)

        // if no form is found, don't even try to convert
        if (html.forms().isEmpty()) {
            logger.warn(getString(Res.string.converter_no_form_found))
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
                logger.error(getString(Res.string.converter_init_writer_error), e)
                return null
            }

        writer.setPdfVersion(PdfWriter.PDF_VERSION_1_7)
        pdf.setMetadata(config)
        pdf.setFirstPageHeaderFooter(config)

        pdf.open()
        pdf.add(Chunk("")) // prevent exception when no content is added

        pdf.setHeaderFooter(config)
        writeIntro(pdf, locationHandler, writer)
        convertForms(html, locationHandler, writer)

        pdf.close()

        return outputStream.toByteArray()
    }

    /**
     * Writes the intro to the PDF. This includes an image and text. The image
     * is scaled to fit the set width and the text is written below the image.
     *
     * @param pdf The PDF document
     * @param locationHandler The location handler
     * @param writer The PDF writer
     */
    private suspend fun writeIntro(
        pdf: PdfDocument,
        locationHandler: LocationHandler,
        writer: PdfWriter,
    ) {
        var spacingBefore = 0f
        if (config.intro.imageEnabled && config.intro.image != null) {
            val image =
                try {
                    Image.getInstance(config.intro.image!!.path)
                } catch (e: BadElementException) {
                    logger.error(getString(Res.string.converter_create_logo_error), e)
                    null
                } catch (e: IOException) {
                    logger.error(getString(Res.string.converter_load_logo_error), e)
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
                        document: PdfDocument?,
                        paragraphPosition: Float,
                    ) {
                        /* this is kind of a hack to get the position of the last paragraph
                         to adjust the location handler accordingly
                         to prevent overlapping with following content */
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

    /**
     * Converts the forms in the given HTML document to PDF form fields and
     * writes them to the PDF.
     *
     * @param html The HTML document
     * @param locationHandler The location handler
     * @param writer The PDF writer
     */
    private suspend fun convertForms(
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

/**
 * Converts the given element to form fields. This is a recursive function
 * that also converts all children of the given element.
 *
 * @param context The context
 * @return The list of form fields or null if the element could not be
 *    converted
 */
suspend fun Element.convert(context: Context): List<FormField>? {
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

        "button" -> {
            val field = convertButton(context)
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

/**
 * Converts the given input element to a form field. This function is a
 * wrapper for the actual conversion functions. It determines the type of
 * the input element and calls the corresponding function.
 *
 * @param context The context
 * @return The form field or null if the input type is not supported
 */
suspend fun Element.convertInput(context: Context): FormField? {
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
                // radio group already exists, radio button was already converted before
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
            context.logger.info(getString(Res.string.converter_input_type_not_supported, attr("type"), id()))
            return null
        }
    }
}

/**
 * Converts the given button element to a form field.
 *
 * @param context The context
 * @return The form field or null if the button type is not supported
 */
suspend fun Element.convertButton(context: Context): FormField? {
    return when (this.attr("type")) {
        "reset" -> {
            reset(this, context)
        }

        "submit" -> {
            submit(this, context)
        }

        else -> {
            context.logger.info(getString(Res.string.converter_button_type_not_supported, attr("type"), id()))
            return null
        }
    }
}

/**
 * Factory function for creating a converter. This is the platform specific
 * implementation for JVM-Desktop using the [HtmlConverter].
 *
 * @param logger The logger to use for logging.
 * @param configContext The configuration context to use for the converter.
 * @return an [HtmlConverter] instance
 */
actual fun createConverter(
    logger: Logger,
    configContext: ConfigContext,
): Converter = HtmlConverter(logger, configContext)
