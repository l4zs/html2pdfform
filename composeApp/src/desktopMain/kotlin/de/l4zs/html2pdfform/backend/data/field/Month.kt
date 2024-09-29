package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_month_format
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

/**
 * A field that represents a month.
 *
 * @param element The HTML element that represents the field.
 * @param context The context of the form.
 * @param defaultFormat The default format of the field.
 * @param id The ID of the field.
 */
class Month(
    element: Element,
    context: Context,
    defaultFormat: String,
    id: Int = context.currentElementIndex,
) : DateTimeField(element, context, id, FieldType.MONTH, defaultFormat)

/**
 * Creates a month form field. If a label is present, it will be used as
 * the field label.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The month form field with label.
 */
suspend fun month(
    element: Element,
    context: Context,
): FieldWithLabel<Month> {
    // use default format from resources
    val month = Month(element, context, getString(Res.string.converter_month_format))
    return FieldWithLabel(month, month.label(), context)
}
