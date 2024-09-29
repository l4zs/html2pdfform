package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_date_format
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

/**
 * Represents a date form field.
 *
 * @param element The HTML element that represents the date field.
 * @param context The context of the form.
 * @param defaultFormat The default format of the date field.
 * @param id The ID of the date field.
 */
class Date(
    element: Element,
    context: Context,
    defaultFormat: String,
    id: Int = context.currentElementIndex,
) : DateTimeField(element, context, id, FieldType.DATE, defaultFormat)

/**
 * Creates a date form field. If a label is present, it will be used as
 * the field label.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The date form field with label.
 */
suspend fun date(
    element: Element,
    context: Context,
): FieldWithLabel<Date> {
    // use default format from resources
    val date = Date(element, context, getString(Res.string.converter_date_format))
    return FieldWithLabel(date, date.label(), context)
}
