package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_time_format
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

/**
 * Represents a time field.
 *
 * @param element The HTML element.
 * @param context The context.
 * @param defaultFormat The default format.
 * @param id The ID.
 */
class Time(
    element: Element,
    context: Context,
    defaultFormat: String,
    id: Int = context.currentElementIndex,
) : DateTimeField(element, context, id, FieldType.TIME, defaultFormat)

/**
 * Creates a time form field. If a label is present, it will be used as
 * the field label.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The time form field with label.
 */
suspend fun time(
    element: Element,
    context: Context,
): FieldWithLabel<Time> {
    // use default format from resources
    val time = Time(element, context, getString(Res.string.converter_time_format))
    return FieldWithLabel(time, time.label(), context)
}
