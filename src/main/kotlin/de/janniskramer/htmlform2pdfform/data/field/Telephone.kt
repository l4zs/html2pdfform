package de.janniskramer.htmlform2pdfform.data.field

import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

// number validation delegated to pattern validation from Text
class Telephone(
    element: Element,
    id: Int,
) : Text(element, id, FieldType.TELEPHONE)

fun FormFields.telephone(
    element: Element,
    context: Context,
): FieldWithLabel<Telephone> {
    val telephone = Telephone(element, context.currentElementIndex)
    return FieldWithLabel(telephone, telephone.label(context))
}
