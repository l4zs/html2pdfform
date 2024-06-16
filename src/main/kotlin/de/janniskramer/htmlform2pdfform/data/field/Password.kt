package de.janniskramer.htmlform2pdfform.data.field

import com.lowagie.text.pdf.PdfFormField
import de.janniskramer.htmlform2pdfform.data.Context
import org.jsoup.nodes.Element

class Password(
    element: Element,
    id: Int,
) : Text(element, id, FieldType.PASSWORD) {
    override fun write(context: Context): PdfFormField {
        val field = super.convert(context)

        field.setFieldFlags(PdfFormField.FF_PASSWORD)

        context.acroForm.addFormField(field)

        return field
    }
}

fun FormFields.password(
    element: Element,
    context: Context,
): FieldWithLabel<Password> {
    val password = Password(element, context.currentElementIndex)
    return FieldWithLabel(password, password.label(context))
}
