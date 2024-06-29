package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Textarea(
    element: Element,
    id: Int,
) : Text(element, id, FieldType.TEXTAREA) {
    override fun write(context: Context): PdfFormField {
        val field = super.convert(context)

        field.setFieldFlags(PdfFormField.FF_MULTILINE)

        context.acroForm.addFormField(field)

        return field
    }
}

fun textarea(
    element: Element,
    context: Context,
): FieldWithLabel<Textarea> {
    val textarea = Textarea(element, context.currentElementIndex)
    return FieldWithLabel(textarea, textarea.label(context))
}
