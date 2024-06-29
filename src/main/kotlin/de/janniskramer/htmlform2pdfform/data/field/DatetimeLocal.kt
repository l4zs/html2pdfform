package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class DatetimeLocal(
    element: Element,
    id: Int,
) : DateTimeField(element, id, FieldType.DATETIME_LOCAL) {
    override fun write(context: Context): PdfFormField = convertDateTime(context, "dd.mm.yyyy HH:MM")
}

fun datetimeLocal(
    element: Element,
    context: Context,
): FieldWithLabel<DatetimeLocal> {
    val datetimeLocal = DatetimeLocal(element, context.currentElementIndex)
    return FieldWithLabel(datetimeLocal, datetimeLocal.label(context))
}
