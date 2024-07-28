package de.janniskramer.htmlform2pdfform.data.field

import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Date(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : DateTimeField(element, context, id, FieldType.DATE, "dd.mm.yyyy")

fun date(
    element: Element,
    context: Context,
): FieldWithLabel<Date> {
    val date = Date(element, context)
    return FieldWithLabel(date, date.label(), context)
}
