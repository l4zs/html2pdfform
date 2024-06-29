package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Date(
    element: Element,
    id: Int,
) : DateTimeField(element, id, FieldType.DATE) {
    override fun write(context: Context): PdfFormField = convertDateTime(context, "dd.mm.yyyy")
}

fun date(
    element: Element,
    context: Context,
): FieldWithLabel<Date> {
    val date = Date(element, context.currentElementIndex)
    return FieldWithLabel(date, date.label(context))
}
