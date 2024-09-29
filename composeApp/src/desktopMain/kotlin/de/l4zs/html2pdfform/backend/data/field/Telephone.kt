package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.data.Context
import org.jsoup.nodes.Element

/**
 * Represents a telephone field.
 *
 * @param element The HTML element representing the field.
 * @param context The context of the field.
 * @param id The unique identifier of the field.
 */
class Telephone(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.TELEPHONE)

/**
 * Creates a telephone form field. If a label is present, it will be used as
 * the field label.
 *
 * @param element The HTML element.
 * @param context The context.
 * @return The telephone form field with label.
 */
fun telephone(
    element: Element,
    context: Context,
): FieldWithLabel<Telephone> {
    val telephone = Telephone(element, context)
    return FieldWithLabel(telephone, telephone.label(), context)
}
