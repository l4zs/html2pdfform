package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import org.jsoup.nodes.Element

class Textarea(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.TEXTAREA) {
    init {
        fieldFlags = fieldFlags or PdfFormField.FF_MULTILINE
    }
}

fun textarea(
    element: Element,
    context: Context,
): FieldWithLabel<Textarea> {
    val textarea = Textarea(element, context)
    return FieldWithLabel(
        textarea,
        textarea.label(),
        context,
    )
}
