package de.l4zs.html2pdfform.backend.data.field

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
        // date/time formatting has to be first
        val formatBefore = additionalActions[PdfFormField.AA_JS_FORMAT] ?: listOf()
        if (type == FieldType.TIME) {
            additionalActions[PdfFormField.AA_JS_FORMAT] =
                mutableListOf(Actions.DateTime.formatTime(format)).plus(formatBefore).toMutableList()
            additionalActions[PdfFormField.AA_JS_KEY]!!.add(Actions.DateTime.keystrokeTime(format))
        } else { // date
            additionalActions[PdfFormField.AA_JS_FORMAT] =
                mutableListOf(Actions.DateTime.formatDate(format)).plus(formatBefore).toMutableList()
            additionalActions[PdfFormField.AA_JS_KEY]!!.add(Actions.DateTime.keystrokeDate(format))
        }
    }
}
