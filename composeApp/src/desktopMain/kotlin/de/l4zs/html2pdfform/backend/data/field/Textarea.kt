package de.l4zs.html2pdfform.backend.data.field

import com.lowagie.text.pdf.PdfFormField
import de.l4zs.html2pdfform.backend.data.Context
import org.jsoup.nodes.Element

/**
 * Represents a textarea field.
 *
 * @param element The HTML element.
 * @param context The context.
 * @param id The field ID.
 */
class Textarea(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.TEXTAREA) {
    init {
        fieldFlags = fieldFlags or PdfFormField.FF_MULTILINE
    }
}

/**
 * Creates a textarea form field. If a label is present, it will be used as
 * the field label.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The textarea form field with label.
 */
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
