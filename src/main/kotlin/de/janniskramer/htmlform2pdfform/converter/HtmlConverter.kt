package de.janniskramer.htmlform2pdfform.converter

import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfDate
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.RGBColor
import de.janniskramer.htmlform2pdfform.Config
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.data.field.FormFields
import de.janniskramer.htmlform2pdfform.data.field.checkbox
import de.janniskramer.htmlform2pdfform.data.field.date
import de.janniskramer.htmlform2pdfform.data.field.datetimeLocal
import de.janniskramer.htmlform2pdfform.data.field.email
import de.janniskramer.htmlform2pdfform.data.field.fakeLabel
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
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File
import java.util.*
import com.lowagie.text.Document as PdfDocument
import org.jsoup.nodes.Document as HtmlDocument

class HtmlConverter {
    private val pdf = PdfDocument(Rectangle(Config.pageWidth, Config.pageHeight))
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
            metaData(pdf)
            headerFooter(pdf)
            pdf.open()

            convertForms(html, writer)
        }
    }

    private fun metaData(pdf: PdfDocument) {
        // TODO: make this configurable
        pdf.addAuthor("Author")
        pdf.addCreator("Creator")
        pdf.addCreationDate(PdfDate(Calendar.getInstance(TimeZone.getDefault())))
        pdf.addSubject("Subject")
        pdf.addProducer("Producer")
    }

    private fun headerFooter(pdf: PdfDocument) {
        val header = Config.header.asPdfHeaderFooter()
        val footer = Config.footer.asPdfHeaderFooter()
        pdf.setHeader(header)
        pdf.setFooter(footer)
    }

    private fun convertForms(
        html: HtmlDocument,
        writer: PdfWriter,
    ) {
        html.forms().forEach { htmlForm ->
            val acroForm = writer.acroForm
            val context =
                Context(
                    locationHandler,
                    this,
                    acroForm,
                    writer,
                )

            htmlForm.convert(context)
        }
    }
}

fun Element.convert(
    context: Context,
    isFieldset: Boolean = false,
) {
    if (this.id().isNotBlank() && context.convertedIds.contains(this.id())) {
        return
    }
    if (this.id().isNotBlank()) {
        context.convertedIds.add(this.id())
    }

    when (this.tagName()) {
        "input" -> convertInput(context, isFieldset)

        "fieldset" -> convertFieldset(context)

        "textarea" -> {
            FormFields.textarea(this, context).write(context)
            context.locationHandler.newLine()
            context.locationHandler.padY(Config.groupPaddingY)
        }

        "signature" -> {
            FormFields.signature(this, context).write(context)
            context.locationHandler.newLine()
            context.locationHandler.padY(Config.groupPaddingY)
        }

        "select" -> {
            FormFields.select(this, context).write(context)
            context.locationHandler.newLine()
            context.locationHandler.padY(Config.groupPaddingY)
        }

        else -> children().forEach { it.convert(context) }
    }
}

fun Element.convertInput(
    context: Context,
    isFieldset: Boolean,
) {
    when (this.attr("type")) {
        "checkbox" -> {
            FormFields.checkbox(this, context)
        }

        "date" -> {
            FormFields.date(this, context)
        }

        "datetime-local" -> {
            FormFields.datetimeLocal(this, context)
        }

        "email" -> {
            FormFields.email(this, context)
        }

        "file" -> {
            FormFields.file(this, context)
        }

        "hidden" -> {
            FormFields.hidden(this, context)
        }

        "month" -> {
            FormFields.month(this, context)
        }

        "number" -> {
            FormFields.number(this, context)
        }

        "password" -> {
            FormFields.password(this, context)
        }

        "radio" -> {
            if (context.radioGroups.containsKey(this.attr("name"))) {
                return
            }
            FormFields.radioGroup(this, context)
        }

        "reset" -> {
            FormFields.reset(this, context)
        }

        "tel" -> {
            FormFields.telephone(this, context)
        }

        "text" -> {
            FormFields.text(this, context)
        }

        "time" -> {
            FormFields.time(this, context)
        }

        "url" -> {
            FormFields.url(this, context)
        }

        else -> return
    }.write(context).also {
        context.locationHandler.newLine()
        if (isFieldset) {
            context.locationHandler.padY(2 * Config.innerPaddingY)
        } else {
            context.locationHandler.padY(Config.groupPaddingY)
        }
    }
}

fun Element.convertFieldset(context: Context) {
    if (this.selectFirst("input[type=radio]") != null) {
        convertRadioFieldset(context)
        return
    }

    val legend = this.selectFirst("legend")
    val fieldSize = this.children().size
    val fieldsetHeight = fieldSize * (Config.fontHeight + Config.innerPaddingY) - Config.innerPaddingY

    writeFieldsetLegendAndBox(context, legend, fieldsetHeight)

    if (legend != null) {
        this.children().minus(legend).forEach { it.convert(context, true) }
    } else {
        this.children().forEach { it.convert(context, true) }
    }
    context.locationHandler.padY(Config.groupPaddingY)
}

fun Element.convertRadioFieldset(context: Context) {
    val legend = this.selectFirst("legend")

    val radioElement = this.selectFirst("input[type=radio]") ?: return
    val radioGroup = FormFields.radioGroup(radioElement, context)

    val fieldsetHeight =
        radioGroup.height +
            if (legend != null) {
                Config.fontHeight / 2 + 2 * Config.innerPaddingY
            } else {
                0f
            }

    writeFieldsetLegendAndBox(context, legend, fieldsetHeight - Config.innerPaddingY)

    radioGroup.write(context).also {
        context.locationHandler.newLine()
        context.locationHandler.padY(Config.innerPaddingY)
    }
    context.locationHandler.padY(Config.groupPaddingY)
}

fun Element.writeFieldsetLegendAndBox(
    context: Context,
    legend: Element?,
    fieldsetHeight: Float,
) {
    if (!context.locationHandler.wouldFitOnPageY(fieldsetHeight)) {
        context.locationHandler.newPage()
    }

    val rectX = Config.pagePaddingX - 2 * Config.innerPaddingX
    val rectY =
        if (legend != null) {
            context.locationHandler.currentY - Config.fontHeight / 2
        } else {
            context.locationHandler.currentY - 2 * Config.innerPaddingY
        }
    val rectWidth = 2 * Config.innerPaddingX + Config.effectivePageWidth + 2 * Config.innerPaddingX
    val rectHeight =
        if (legend != null) {
            fieldsetHeight + 2 * Config.innerPaddingY - (Config.fontHeight / 2 - 2 * Config.innerPaddingY)
        } else {
            2 * Config.innerPaddingY + fieldsetHeight + 2 * Config.innerPaddingY
        }

    if (legend != null) {
        FormFields.fakeLabel(context, legend.text()).write(context).also {
            context.locationHandler.newLine()
            context.locationHandler.padY(Config.innerPaddingY)
        }
    } else {
        context.locationHandler.padY(2 * Config.innerPaddingY)
        context.locationHandler.padY(2 * Config.innerPaddingY)
    }

    val cb = context.writer.directContent
    cb.setLineWidth(1f)
    cb.setColorStroke(RGBColor(128, 128, 128))
    cb.moveTo(rectX, rectY)
    if (legend != null) {
        cb.lineTo(rectX + Config.innerPaddingX, rectY)
        cb.moveTo(
            rectX + Config.innerPaddingX + Config.defaultFontWidth * legend.text().length + Config.innerPaddingX,
            rectY,
        )
    }
    cb.lineTo(rectX + rectWidth, rectY)
    cb.lineTo(rectX + rectWidth, rectY - rectHeight)
    cb.lineTo(rectX, rectY - rectHeight)
    cb.lineTo(rectX, rectY)
    cb.stroke()
    cb.sanityCheck()
}
