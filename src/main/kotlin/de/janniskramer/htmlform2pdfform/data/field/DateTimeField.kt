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
                if (type == FieldType.TIME) {
                    Actions.DateTime.formatTime(format ?: defaultFormat)
                } else {
                    Actions.DateTime.formatDate(format ?: defaultFormat)
                },
                context.writer,
            ),
        )

        // on keystroke format, so that inputting "1:4" will change to "01:04"
        field.setAdditionalActions(
            PdfFormField.AA_JS_KEY,
            PdfAction.javaScript(
                if (type == FieldType.TIME) {
                    Actions.DateTime.formatTime(format ?: defaultFormat)
                } else {
                    Actions.DateTime.formatDate(format ?: defaultFormat)
                },
                context.writer,
            ),
        )

        // TODO: min, max, step validation

        context.acroForm.addFormField(field)

        return field
    }
}
