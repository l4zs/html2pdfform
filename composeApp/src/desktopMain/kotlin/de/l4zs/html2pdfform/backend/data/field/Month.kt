package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_month_format
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

class Month(
    element: Element,
    context: Context,
    defaultFormat: String,
    id: Int = context.currentElementIndex,
) : DateTimeField(element, context, id, FieldType.MONTH, defaultFormat)

suspend fun month(
    element: Element,
    context: Context,
): FieldWithLabel<Month> {
    val month = Month(element, context, getString(Res.string.converter_month_format))
    return FieldWithLabel(month, month.label(), context)
}
