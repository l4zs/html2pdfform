package de.l4zs.html2pdfform.data.field

import de.l4zs.html2pdfform.data.Context
import org.jsoup.nodes.Element

class Month(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : DateTimeField(element, context, id, FieldType.MONTH, "mmm yyyy")

fun month(
    element: Element,
    context: Context,
): FieldWithLabel<Month> {
    val month = Month(element, context)
    return FieldWithLabel(month, month.label(), context)
}
