package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_datetime_format
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

/**
 * A field for date and time input.
 *
 * @param element The HTML element.
 * @param context The context.
 * @param defaultFormat The default format for the date and time.
 * @param id The ID of the field.
 */
class DatetimeLocal(
    element: Element,
    context: Context,
    defaultFormat: String,
    id: Int = context.currentElementIndex,
) : DateTimeField(element, context, id, FieldType.DATETIME_LOCAL, defaultFormat)

/**
 * Creates a datetime form field. If a label is present, it will be used as
 * the field label.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The datetime form field with label.
 */
suspend fun datetimeLocal(
    element: Element,
    context: Context,
): FieldWithLabel<DatetimeLocal> {
    // use default format from resources
    val datetimeLocal = DatetimeLocal(element, context, getString(Res.string.converter_datetime_format))
    return FieldWithLabel(datetimeLocal, datetimeLocal.label(), context)
}
