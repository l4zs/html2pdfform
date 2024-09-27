package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import org.jsoup.nodes.Element

class Hidden(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.HIDDEN) {
    init {
        field.addFlags(PdfFormField.FLAGS_HIDDEN)
    }
}

fun hidden(
    element: Element,
    context: Context,
): FieldWithLabel<Hidden> {
    val field = Hidden(element, context)
    return FieldWithLabel(field, field.label(), context)
}
