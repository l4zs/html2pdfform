package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import org.jsoup.nodes.Element

class Textarea(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : de.l4zs.html2pdfform.backend.data.field.Text(element, context, id,
    _root_ide_package_.de.l4zs.html2pdfform.backend.data.field.FieldType.TEXTAREA
) {
    init {
        field.setFieldFlags(PdfFormField.FF_MULTILINE)
    }
}

fun textarea(
    element: Element,
    context: Context,
): de.l4zs.html2pdfform.backend.data.field.FieldWithLabel<de.l4zs.html2pdfform.backend.data.field.Textarea> {
    val textarea = _root_ide_package_.de.l4zs.html2pdfform.backend.data.field.Textarea(element, context)
    return _root_ide_package_.de.l4zs.html2pdfform.backend.data.field.FieldWithLabel(
        textarea,
        textarea.label(),
        context
    )
}
