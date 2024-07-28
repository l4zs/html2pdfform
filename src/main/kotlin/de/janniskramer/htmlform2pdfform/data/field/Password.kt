package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Password(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.PASSWORD) {
    init {
        field.setFieldFlags(PdfFormField.FF_PASSWORD)
    }
}

fun password(
    element: Element,
    context: Context,
): FieldWithLabel<Password> {
    val password = Password(element, context)
    return FieldWithLabel(password, password.label(), context)
}
