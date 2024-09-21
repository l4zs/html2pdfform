package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfAction
import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.util.Actions
import org.jsoup.nodes.Element

abstract class DateTimeField(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
    type: FieldType,
    defaultFormat: String,
) : Text(element, context, id, type) {
    private val format: String = element.attr("format").ifBlank { defaultFormat }

    init {
        field.setAdditionalActions(
            PdfFormField.AA_JS_FORMAT,
            PdfAction.javaScript(
                if (type == FieldType.TIME) {
                    Actions.DateTime.formatTime(format)
                } else {
                    Actions.DateTime.formatDate(format)
                },
                context.writer,
            ),
        )

        // on keystroke format, so that inputting "1:4" will change to "01:04"
        field.setAdditionalActions(
            PdfFormField.AA_JS_KEY,
            PdfAction.javaScript(
                if (type == FieldType.TIME) {
                    Actions.DateTime.formatTime(format)
                } else {
                    Actions.DateTime.formatDate(format)
                },
                context.writer,
            ),
        )
    }
}
