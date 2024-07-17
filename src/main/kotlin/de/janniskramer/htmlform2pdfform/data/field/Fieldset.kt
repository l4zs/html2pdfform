package de.janniskramer.htmlform2pdfform.data.field

import de.janniskramer.htmlform2pdfform.converter.convert
import de.janniskramer.htmlform2pdfform.data.Context
import de.janniskramer.htmlform2pdfform.data.Rectangle
import org.jsoup.nodes.Element

class Fieldset(
    element: Element,
    context: Context,
) : FormField(FieldType.FIELDSET, element, context.currentElementIndex, context) {
    val fields = mutableListOf<FormField>()

    override fun write() {
        rectangle = Rectangle(0f, 0f, 0f, 0f)
        super.write()
    }
}

fun fieldset(
    element: Element,
    context: Context,
): Fieldset {
    val field = Fieldset(element, context)
    field.fields.addAll(
        element
            .children()
            .mapNotNull { it.convert(context) }
            .flatten(),
    )
    return field
}
