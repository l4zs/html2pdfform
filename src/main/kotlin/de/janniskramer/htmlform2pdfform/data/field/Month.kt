package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Month(
    element: Element,
    id: Int,
) : DateTimeField(element, id, FieldType.MONTH) {
    override fun write(context: Context): PdfFormField = convertDateTime(context, "mmm yyyy")
}

fun month(
    element: Element,
    context: Context,
): FieldWithLabel<Month> {
    val month = Month(element, context.currentElementIndex)
    return FieldWithLabel(month, month.label(context))
}
