package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import org.jsoup.nodes.Element

/**
 * Represents a file field.
 *
 * @param element The HTML element.
 * @param context The context.
 * @param id The field ID.
 */
class File(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.FILE)

/**
 * Creates a file form field. If a label is present, it will be used as
 * the field label.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The file form field with label.
 */
fun file(
    element: Element,
    context: Context,
): FieldWithLabel<File> {
    val file = File(element, context)
    return FieldWithLabel(file, file.label(), context)
}
