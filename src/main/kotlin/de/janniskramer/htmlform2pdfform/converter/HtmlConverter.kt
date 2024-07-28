package de.janniskramer.htmlform2pdfform.converter

import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.Image
import com.lowagie.text.Paragraph
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfWriter
import de.janniskramer.htmlform2pdfform.config
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.data.field.FormField
import de.janniskramer.htmlform2pdfform.data.field.checkbox
import de.janniskramer.htmlform2pdfform.data.field.date
import de.janniskramer.htmlform2pdfform.data.field.datetimeLocal
import de.janniskramer.htmlform2pdfform.data.field.email
import de.janniskramer.htmlform2pdfform.data.field.fieldset
import de.janniskramer.htmlform2pdfform.data.field.file
import de.janniskramer.htmlform2pdfform.data.field.hidden
import de.janniskramer.htmlform2pdfform.data.field.month
import de.janniskramer.htmlform2pdfform.data.field.number
import de.janniskramer.htmlform2pdfform.data.field.password
import de.janniskramer.htmlform2pdfform.data.field.radioGroup
import de.janniskramer.htmlform2pdfform.data.field.reset
import de.janniskramer.htmlform2pdfform.data.field.select
import de.janniskramer.htmlform2pdfform.data.field.signature
import de.janniskramer.htmlform2pdfform.data.field.telephone
import de.janniskramer.htmlform2pdfform.data.field.text
import de.janniskramer.htmlform2pdfform.data.field.textarea
import de.janniskramer.htmlform2pdfform.data.field.time
import de.janniskramer.htmlform2pdfform.data.field.url
import de.janniskramer.htmlform2pdfform.extensions.setFirstPageHeaderFooter
import de.janniskramer.htmlform2pdfform.extensions.setHeaderFooter
import de.janniskramer.htmlform2pdfform.extensions.setMetadata
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File
import com.lowagie.text.Document as PdfDocument
import org.jsoup.nodes.Document as HtmlDocument

class HtmlConverter {
    private val pdf =
        PdfDocument(
            Rectangle(config.pageWidth, config.pageHeight),
            config.pagePaddingX,
            config.pagePaddingX,
            config.pagePaddingY / 2,
            config.pagePaddingY / 2,
        )
    private val locationHandler = LocationHandler(pdf)

    fun parse(
        input: File,
        output: File,
    ) {
        val html =
            try {
                Jsoup.parse(input)
            } catch (exception: java.io.IOException) {
                return // TODO: error handling
            }

        pdf.use {
            val writer = PdfWriter.getInstance(pdf, output.outputStream())
            writer.setPdfVersion(PdfWriter.PDF_VERSION_1_7)
            it.setMetadata()
            it.setFirstPageHeaderFooter()
            it.open()
            it.setHeaderFooter()

            writeIntro(writer)

            convertForms(html, writer)
        }
    }

    /**
     * Writes the intro text to the PDF. (optional Image and text at the
     * beginning of the PDF)
     */
    private fun writeIntro(writer: PdfWriter) {
        var spacingBefore = 0f
        if (config.intro.image != null) {
            val image = Image.getInstance(config.intro.image!!.path)
            image.scaleToFit(config.intro.image!!.width, config.intro.image!!.height)
            image.setAbsolutePosition(
                config.pageMinX,
                config.pageMaxY - image.scaledHeight,
            )
            pdf.add(image)
            spacingBefore += image.scaledHeight
        }
        if (config.intro.text != null) {
            writer.pageEvent =
                object : com.lowagie.text.pdf.PdfPageEventHelper() {
                    override fun onParagraphEnd(
                        writer: PdfWriter?,
                        document: Document?,
                        paragraphPosition: Float,
                    ) {
                        // TODO this is a hack to get the position of the last paragraph
                        locationHandler.currentY = paragraphPosition - config.groupPaddingY
                        println(paragraphPosition)
                    }
                }
            val paragraph = Paragraph(config.intro.text!!.text)
            paragraph.spacingBefore = spacingBefore + config.groupPaddingY
            paragraph.font = Font(config.baseFont, config.intro.text!!.fontSize)
            paragraph.alignment = Paragraph.ALIGN_LEFT or Paragraph.ALIGN_TOP
            pdf.add(paragraph)
        }
    }

    private fun convertForms(
        html: HtmlDocument,
        writer: PdfWriter,
    ) {
        html.forms().forEach { htmlForm ->
            val context =
                Context(
                    locationHandler,
                    this,
                    writer.acroForm,
                    writer,
                )

            htmlForm.convert(context)?.forEach {
                println(it.element)
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

        else -> return null
    }
}
