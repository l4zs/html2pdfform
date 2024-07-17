package de.janniskramer.htmlform2pdfform.converter

import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.RGBColor
import de.janniskramer.htmlform2pdfform.config
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.data.field.FormField
import de.janniskramer.htmlform2pdfform.data.field.checkbox
import de.janniskramer.htmlform2pdfform.data.field.date
import de.janniskramer.htmlform2pdfform.data.field.datetimeLocal
import de.janniskramer.htmlform2pdfform.data.field.email
import de.janniskramer.htmlform2pdfform.data.field.fakeLabel
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
import de.janniskramer.htmlform2pdfform.extensions.setHeaderFooter
import de.janniskramer.htmlform2pdfform.extensions.setMetadata
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File
import com.lowagie.text.Document as PdfDocument
import org.jsoup.nodes.Document as HtmlDocument

class HtmlConverter {
    private val pdf = PdfDocument(Rectangle(config.pageWidth, config.pageHeight))
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
            it.setHeaderFooter()
            it.open()

            convertForms(html, writer)
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

            htmlForm.convert(context)
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

fun Element.convertFieldset(
    context: Context,
    isInsideFieldset: Boolean,
) {
    if (this.selectFirst("input[type=radio]") != null) {
        convertRadioFieldset(context)
        return
    }

    val legend = this.selectFirst("legend")
    val fieldSize = this.children().size
    val fieldsetHeight = fieldSize * (config.fontHeight + config.innerPaddingY) - config.innerPaddingY

    writeFieldsetLegendAndBox(context, legend, fieldsetHeight, isInsideFieldset)

    if (legend != null) {
        this.children().minus(legend).forEach { it.convert(context, true) }
    } else {
        this.children().forEach { it.convert(context, true) }
    }
    context.locationHandler.padY(config.groupPaddingY)
}

fun Element.convertRadioFieldset(context: Context) {
    val legend = this.selectFirst("legend")

    val radioElement = this.selectFirst("input[type=radio]") ?: return
    val radioGroup = radioGroup(radioElement, context)

    val fieldsetHeight =
        radioGroup.height +
            if (legend != null) {
                config.fontHeight / 2 + 2 * config.innerPaddingY
            } else {
                0f
            }

    writeFieldsetLegendAndBox(context, legend, fieldsetHeight - config.innerPaddingY)

    radioGroup.write(context).also {
        context.locationHandler.newLine()
        context.locationHandler.padY(config.innerPaddingY)
    }
    context.locationHandler.padY(config.groupPaddingY)
}

fun Element.writeFieldsetLegendAndBox(
    context: Context,
    legend: Element?,
    fieldsetHeight: Float,
    isInsideFieldset: Boolean = false,
) {
    if (!context.locationHandler.wouldFitOnPageY(fieldsetHeight)) {
        context.locationHandler.newPage()
    }

    val rectX = config.pagePaddingX - 2 * config.innerPaddingX
    val rectY =
        if (legend != null) {
            context.locationHandler.currentY - config.fontHeight / 2
        } else {
            context.locationHandler.currentY - 2 * config.innerPaddingY
        }
    val rectWidth = 2 * config.innerPaddingX + config.effectivePageWidth + 2 * config.innerPaddingX
    val rectHeight =
        if (legend != null) {
            fieldsetHeight + 2 * config.innerPaddingY - (config.fontHeight / 2 - 2 * config.innerPaddingY)
        } else {
            2 * config.innerPaddingY + fieldsetHeight + 2 * config.innerPaddingY
        }

    if (legend != null) {
        fakeLabel(context, legend.text()).write(context).also {
            context.locationHandler.newLine()
            context.locationHandler.padY(config.innerPaddingY)
        }
    } else {
        context.locationHandler.padY(2 * config.innerPaddingY)
        context.locationHandler.padY(2 * config.innerPaddingY)
    }

    val cb = context.writer.directContent
    cb.setLineWidth(1f)
    cb.setColorStroke(RGBColor(128, 128, 128))
    cb.moveTo(rectX, rectY)
    if (legend != null) {
        cb.lineTo(rectX + config.innerPaddingX, rectY)
        cb.moveTo(
            rectX + config.innerPaddingX + config.baseFont.getWidthPoint(legend.text(), config.fontSize) + config.innerPaddingX,
            rectY,
        )
    }
    cb.lineTo(rectX + rectWidth, rectY)
    if (!isInsideFieldset) {
        cb.lineTo(rectX + rectWidth, rectY - rectHeight)
        cb.lineTo(rectX, rectY - rectHeight)
        cb.lineTo(rectX, rectY)
    }
    cb.stroke()
    cb.sanityCheck()
}
