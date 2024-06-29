package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Time(
    element: Element,
    id: Int,
) : DateTimeField(element, id, FieldType.TIME) {
    override fun write(context: Context): PdfFormField = convertDateTime(context, "HH:MM")
}

fun time(
    element: Element,
    context: Context,
): FieldWithLabel<Time> {
    val time = Time(element, context.currentElementIndex)
    return FieldWithLabel(time, time.label(context))
}
