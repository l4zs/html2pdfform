package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_datetime_format
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

class DatetimeLocal(
    element: Element,
    context: Context,
    defaultFormat: String,
    id: Int = context.currentElementIndex,
) : DateTimeField(element, context, id, FieldType.DATETIME_LOCAL, defaultFormat)

suspend fun datetimeLocal(
    element: Element,
    context: Context,
): FieldWithLabel<DatetimeLocal> {
    val datetimeLocal = DatetimeLocal(element, context, getString(Res.string.converter_datetime_format))
    return FieldWithLabel(datetimeLocal, datetimeLocal.label(), context)
}
