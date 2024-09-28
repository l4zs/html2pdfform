package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_date_format
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

class Date(
    element: Element,
    context: Context,
    defaultFormat: String,
    id: Int = context.currentElementIndex,
) : DateTimeField(element, context, id, FieldType.DATE, defaultFormat)

suspend fun date(
    element: Element,
    context: Context,
): FieldWithLabel<Date> {
    val date = Date(element, context, getString(Res.string.converter_date_format))
    return FieldWithLabel(date, date.label(), context)
}
