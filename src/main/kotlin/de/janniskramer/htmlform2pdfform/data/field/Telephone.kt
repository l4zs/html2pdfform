package de.janniskramer.htmlform2pdfform.data.field

import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

// number validation delegated to pattern validation from Text
class Telephone(
    element: Element,
    context: Context,
    id: Int = context.currentElementIndex,
) : Text(element, context, id, FieldType.TELEPHONE)

fun telephone(
    element: Element,
    context: Context,
): FieldWithLabel<Telephone> {
    val telephone = Telephone(element, context)
    return FieldWithLabel(telephone, telephone.label(), context)
}
