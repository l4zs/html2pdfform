package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.converter_time_format
import org.jetbrains.compose.resources.getString
import org.jsoup.nodes.Element

class Time(
    element: Element,
    context: Context,
    defaultFormat: String,
    id: Int = context.currentElementIndex,
) : DateTimeField(element, context, id, FieldType.TIME, defaultFormat)

suspend fun time(
    element: Element,
    context: Context,
): FieldWithLabel<Time> {
    val time = Time(element, context, getString(Res.string.converter_time_format))
    return FieldWithLabel(time, time.label(), context)
}
