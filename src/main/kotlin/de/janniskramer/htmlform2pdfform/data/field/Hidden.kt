package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Hidden(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.HIDDEN) {
    init {
        field.setFlags(PdfFormField.FLAGS_HIDDEN)
    }
}

fun hidden(
    element: Element,
    context: Context,
): FieldWithLabel<Hidden> {
    val hidden = Hidden(element, context)
    return FieldWithLabel(hidden, hidden.label(), context)
}
