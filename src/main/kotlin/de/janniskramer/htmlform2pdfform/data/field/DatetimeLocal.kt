package de.janniskramer.htmlform2pdfform.data.field

import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class DatetimeLocal(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : DateTimeField(element, context, id, FieldType.DATETIME_LOCAL, "dd.mm.yyyy HH:MM")

fun datetimeLocal(
    element: Element,
    context: Context,
): FieldWithLabel<DatetimeLocal> {
    val datetimeLocal = DatetimeLocal(element, context)
    return FieldWithLabel(datetimeLocal, datetimeLocal.label(), context)
}
