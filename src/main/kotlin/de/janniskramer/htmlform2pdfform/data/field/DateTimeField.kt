package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Actions
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

abstract class DateTimeField(
    element: Element,
    id: Int,
    type: FieldType,
) : Text(element, id, type) {
    private val format: String? = element.attr("format").ifBlank { null }

    fun convertDateTime(
        context: Context,
        defaultFormat: String,
    ): PdfFormField {
        val field = super.convert(context)

        field.setAdditionalActions(
            PdfFormField.AA_JS_FORMAT,
            PdfAction.javaScript(
                Actions.DateTime.formatDateTime(format ?: defaultFormat),
                context.writer,
            ),
        )

        field.setAdditionalActions(
            PdfFormField.AA_JS_KEY,
            PdfAction.javaScript(
                Actions.DateTime.keystrokeDateTime(format ?: defaultFormat),
                context.writer,
            ),
        )

        // TODO: min, max, step validation

        context.acroForm.addFormField(field)

        return field
    }
}
