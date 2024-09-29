package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import org.jsoup.nodes.Element

/**
 * Represents a password field.
 *
 * @param element The HTML element.
 * @param context The context.
 * @param id The ID.
 */
class Password(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.PASSWORD) {
    init {
        fieldFlags = fieldFlags or PdfFormField.FF_PASSWORD
    }
}

/**
 * Creates a password form field. If a label is present, it will be used as
 * the field label.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The password form field with label.
 */
fun password(
    element: Element,
    context: Context,
): FieldWithLabel<Password> {
    val password = Password(element, context)
    return FieldWithLabel(password, password.label(), context)
}
