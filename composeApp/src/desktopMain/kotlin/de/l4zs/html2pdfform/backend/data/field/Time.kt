package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import org.jsoup.nodes.Element

class Time(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : DateTimeField(element, context, id, FieldType.TIME, "HH:MM")

fun time(
    element: Element,
    context: Context,
): FieldWithLabel<Time> {
    val time = Time(element, context)
    return FieldWithLabel(time, time.label(), context)
}
