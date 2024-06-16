package de.janniskramer.htmlform2pdfform.converter

import com.lowagie.text.pdf.PdfWriter
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
import com.lowagie.text.Document as PdfDocument
import org.jsoup.nodes.Document as HtmlDocument

class HtmlConverter {
    private val pdf = PdfDocument()
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
            pdf.open()

            convertForms(html, writer)
        }
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

fun Element.convert(context: Context) {
    if (this.id().isNotBlank() && context.convertedIds.contains(this.id())) {
        return
    }
    if (this.id().isNotBlank()) {
        context.convertedIds.add(this.id())
    }

    when (this.tagName()) {
        "input" -> {
            convertInput(context)
        }

        "fieldset" -> {
            // legend
            val legend = this.selectFirst("legend")
            val fieldSize = this.children().size
            val fieldsetHeight = fieldSize * (Config.fontHeight + Config.innerPaddingY) - Config.innerPaddingY
            if (!context.locationHandler.wouldFitOnPageY(fieldsetHeight)) {
                context.locationHandler.newPage()
            }

            if (legend != null) {
                FormFields.fakeLabel(context, legend.text()).write(context).also {
                    context.locationHandler.newLine()
                    context.locationHandler.padY(Config.innerPaddingY)
                }
            }

            // fields
            if (legend != null) {
                this.children().minus(legend).forEach { it.convert(context) }
            } else {
                this.children().forEach { it.convert(context) }
            }
        }

        "textarea" -> {
            FormFields.textarea(this, context).write(context).also {
                context.locationHandler.newLine()
                context.locationHandler.padY(Config.groupPaddingY)
            }
        }

        "signature" -> {
            FormFields.signature(this, context).write(context).also {
                context.locationHandler.newLine()
                context.locationHandler.padY(Config.groupPaddingY)
            }
        }

        "select" -> {
            FormFields.select(this, context).write(context).also {
                context.locationHandler.newLine()
                context.locationHandler.padY(Config.groupPaddingY)
            }
        }

        else -> {
            children().forEach { it.convert(context) }
        }
    }
}

fun Element.convertInput(context: Context) {
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
        context.locationHandler.padY(Config.groupPaddingY)
    }
}
