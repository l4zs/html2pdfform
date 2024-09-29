package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.util.Actions
import org.jsoup.nodes.Element

/**
 * Represents a date or time field.
 *
 * @param element The HTML element that represents the field.
 * @param context The context of the field.
 * @param id The ID of the field.
 * @param type The type of the field.
 * @param defaultFormat The default format of the field.
 */
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
