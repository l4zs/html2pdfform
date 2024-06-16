package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Hidden(
    element: Element,
    id: Int,
) : Text(element, id, FieldType.HIDDEN) {
    override fun write(context: Context): PdfFormField {
        val field = super.convert(context)

        field.setFlags(PdfFormField.FLAGS_HIDDEN)

        context.acroForm.addFormField(field)

        return field
    }
}

fun FormFields.hidden(
    element: Element,
    context: Context,
): FieldWithLabel<Hidden> {
    val hidden = Hidden(element, context.currentElementIndex)
    return FieldWithLabel(hidden, hidden.label(context))
}
